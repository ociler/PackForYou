package com.packforyou.data.repositories

import com.packforyou.api.DirectionsApiService
import com.packforyou.api.DistanceMatrixApiService
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.models.*
import com.packforyou.ui.packages.IGetLeg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


interface IPackagesRepository {
    suspend fun getDeliveryManPackages(deliveryManId: String): List<Package>?
    suspend fun addPackage(packge: Package)
    suspend fun getOptimizedRoute(route: Route): Route?

    suspend fun computeDistanceBetweenAllPackages(packages: List<Package>, callback: IGetLeg)
    suspend fun computeDistanceBetweenStartLocationAndPackages(
        startLocation: Location,
        endLocation: Location,
        packages: List<Package>,
        callback: IGetLeg
    )

    suspend fun computeDistanceBetweenEndLocationAndPackages(
        endLocation: Location,
        packages: List<Package>,
        callback: IGetLeg
    )
}

class PackagesRepositoryImpl(
    private val dataSource: IFirebaseRemoteDatabase,
    private val directionsApiService: DirectionsApiService,
    private val distanceMatrixApiService: DistanceMatrixApiService
) : IPackagesRepository {

    private lateinit var travelTimeArray: Array<IntArray>
    private lateinit var startTravelTimeArray: IntArray
    private lateinit var endTravelTimeArray: IntArray

    private lateinit var globalPackagesList: List<Package>
    private lateinit var globalStartLocation: Location
    private lateinit var globalEndLocation: Location

    private var finishedCalls = 0
    private var totalCalls = 0
    private lateinit var globalCallback: IGetLeg

    override suspend fun getDeliveryManPackages(deliveryManId: String): List<Package>? {
        var packages: List<Package>? = null

        dataSource.getDeliveryManPackages(deliveryManId).collect { state ->
            when (state) {
                is State.Loading -> {
                    println("Wait! It's loading")
                }

                is State.Success -> {
                    packages = state.data
                }

                is State.Failed -> println("Failed! ${state.message}")
            }
        }
        return packages
    }

    private fun initializeTravelTimeArray(size: Int) {
        travelTimeArray = Array(size) { IntArray(size) }
    }

    private fun initializeStartTravelTimeArray(size: Int) {
        startTravelTimeArray = IntArray(size)
    }

    private fun initializeEndTravelTimeArray(size: Int) {
        endTravelTimeArray = IntArray(size)
    }

    override suspend fun addPackage(packge: Package) {
        dataSource.addPackage(packge).collect { state ->
            when (state) {
                is State.Loading -> {
                }

                is State.Success -> {
                    println("Package Added")
                }

                is State.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
        }
    }

    override suspend fun getOptimizedRoute(route: Route): Route? {
        if (route.packages == null) {
            return null
        }


        val oAddress = getFormattedAddress(route.deliveryMan?.currentLocation)
        val dAddress = getFormattedAddress(route.deliveryMan?.endLocation)
        val routeLocations = getLocationsFromPackages(route.packages)
        var waypoints = getFormattedWaypointsByAddress(routeLocations)
        waypoints =
            "optimize:true|$waypoints" //TODO esto no deuria ser així. Deuria estar en la pròpia crida a la API

        val optimizedRouteResponse =
            directionsApiService.getOptimizedRoute(oAddress, dAddress, waypoints)

        val sortedList = arrayListOf<Package>()
        val sortedOrder = optimizedRouteResponse.routes.last().waypoint_order

        for (i in 0 until route.packages!!.size) {
            sortedList.add(route.packages!![sortedOrder[i]])
        }

        return route.copy(packages = sortedList)
    }

    private suspend fun enqueuePackagesLeg(originPackage: Package, destinationPackage: Package) {

        getLeg(originPackage.location!!, destinationPackage.location!!).collect { state ->
            when (state) {
                is State.Loading -> {
                }

                is State.Success -> {
                    //ya tenemos el dato
                    travelTimeArray[originPackage.numPackage][destinationPackage.numPackage] =
                        state.data.duration!!

                    synchronized(this/*we want to block the thread. Doesn't mind the variable we put here*/) {
                        finishedCalls++
                        if (finishedCalls == totalCalls) {
                            globalCallback.onSuccessBetweenPackages(travelTimeArray)
                        }
                    }

                }

                is State.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
            //if I place here the synchronous block of code, there is some problem. I have to place in the State.Success case
        }
    }

    private suspend fun enqueueStartLeg(originLocation: Location, destinationPackage: Package) {
        if (destinationPackage.location == null) return

        getLeg(originLocation, destinationPackage.location!!).collect { state ->
            when (state) {
                is State.Loading -> {
                }

                is State.Success -> {
                    //ya tenemos el dato
                    startTravelTimeArray[destinationPackage.numPackage] = state.data.duration!!

                    synchronized(this/*we want to block the thread. Doesn't mind the variable we put here*/) {
                        finishedCalls++
                        if (finishedCalls == totalCalls) {
                            globalCallback.onSuccessBetweenStartLocationAndPackages(
                                startTravelTimeArray, globalEndLocation, globalPackagesList
                            )
                        }
                    }

                }

                is State.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
            //if I place here the synchronous block of code, there is some problem. I have to place in the State.Success case
        }
    }


    private suspend fun enqueueEndLeg(originPackage: Package, destinationLocation: Location) {
        if (originPackage.location == null) return

        getLeg(originPackage.location!!, destinationLocation).collect { state ->
            when (state) {
                is State.Loading -> {
                }

                is State.Success -> {
                    //ya tenemos el dato
                    endTravelTimeArray[originPackage.numPackage] = state.data.duration!!

                    synchronized(this/*we want to block the thread. Doesn't mind the variable we put here*/) {
                        finishedCalls++
                        if (finishedCalls == totalCalls) {
                            globalCallback.onSuccessBetweenEndLocationAndPackages(
                                endTravelTimeArray, globalPackagesList
                            )
                        }
                    }

                }

                is State.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
            //if I place here the synchronous block of code, there is some problem. I have to place in the State.Success case
        }
    }

    private suspend fun getLeg(origin: Location, destination: Location): Flow<State<Leg>> {
        return flow {
            emit(State.loading())

            val oLatLong = getStringLatLongFromLocation(origin)
            val dLatLong = getStringLatLongFromLocation(destination)

            val distanceAndTime = distanceMatrixApiService.getDistance(oLatLong, dLatLong)

            val leg = Leg(
                distanceAndTime.rows[0].elements[0].distance.value,
                distanceAndTime.rows[0].elements[0].duration.value
            )
            emit(State.success(leg))

        }.catch {
            // If exception is thrown, emit failed state along with message.
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun computeDistanceBetweenAllPackages(
        packages: List<Package>,
        callback: IGetLeg
    ) {
        globalCallback = callback
        globalPackagesList = packages

        totalCalls = packages.size * packages.size
        finishedCalls = 0
        initializeTravelTimeArray(packages.size)

        for (origin in packages) {
            for (destination in packages) {
                if (origin.numPackage != destination.numPackage &&
                    origin.location != null && destination.location != null
                ) {
                    enqueuePackagesLeg(origin, destination)
                } else {
                    synchronized(this) {
                        finishedCalls++
                        if (finishedCalls == totalCalls) {
                            globalCallback.onSuccessBetweenPackages(travelTimeArray)
                        }
                    }
                }
            }
        }
    }

    //Here we need the endLocation as well because we have no other way to get this Location
    override suspend fun computeDistanceBetweenStartLocationAndPackages(
        startLocation: Location,
        endLocation: Location,
        packages: List<Package>,
        callback: IGetLeg
    ) {
        globalCallback = callback
        globalPackagesList = packages
        globalStartLocation = startLocation
        globalEndLocation = endLocation

        totalCalls = packages.size
        finishedCalls = 0
        initializeStartTravelTimeArray(packages.size)

        for (destination in packages) {
            if (destination.location != null) {
                enqueueStartLeg(startLocation, destination)
            } else {
                synchronized(this) {
                    finishedCalls++
                    if (finishedCalls == totalCalls) {
                        globalCallback.onSuccessBetweenStartLocationAndPackages(
                            startTravelTimeArray,
                            globalEndLocation,
                            globalPackagesList
                        )
                    }
                }
            }
        }
    }

    override suspend fun computeDistanceBetweenEndLocationAndPackages(
        endLocation: Location,
        packages: List<Package>,
        callback: IGetLeg
    ) {
        globalCallback = callback
        globalPackagesList = packages
        globalEndLocation = endLocation

        totalCalls = packages.size
        finishedCalls = 0
        initializeEndTravelTimeArray(packages.size)

        for (origin in packages) {
            if (origin.location != null) {
                enqueueEndLeg(origin, endLocation)
            } else {
                synchronized(this) {
                    finishedCalls++
                    if (finishedCalls == totalCalls) {
                        globalCallback.onSuccessBetweenEndLocationAndPackages(
                            endTravelTimeArray,
                            globalPackagesList
                        )
                    }
                }
            }
        }
    }

    private fun getStringLatLongFromLocation(location: Location?): String {
        return if (location != null) {
            "${location.latitude},${location.longitude}"
        } else {
            ""
        }
    }

    private fun getFormattedAddress(location: Location?): String {
        return location?.address?.replace(' ', '+') ?: ""
    }

    private fun getFormattedWaypointsByLocation(locations: List<Location?>): String {
        var result = ""

        result = getStringLatLongFromLocation(locations[0])

        for (location in locations.subList(1, locations.size)) {
            result += "|${getStringLatLongFromLocation(location)}"
        }
        return result
    }

    private fun getFormattedWaypointsByAddress(locations: List<Location?>): String {
        var result = ""

        result = getFormattedAddress(locations[0])

        for (location in locations.subList(1, locations.size)) {
            result += "|${getFormattedAddress(location)}"
        }
        return result
    }

    private fun getLocationsFromPackages(packages: List<Package>?): List<Location?> {
        val locations = arrayListOf<Location?>()
        if (packages != null) {
            for (packge in packages) {
                locations.add(packge.location)
            }
        }
        return locations.toList()
    }
}