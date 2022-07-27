package com.packforyou.data.repositories

import com.packforyou.api.DirectionsApiService
import com.packforyou.api.DistanceMatrixApiService
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.models.*
import com.packforyou.api.ICallbackAPICalls
import com.packforyou.api.ICallbackDirectionsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Singleton


interface IPackagesAndAtlasRepository {
    suspend fun getDeliveryManPackages(deliveryManId: String): List<Package>?
    suspend fun addPackage(packge: Package)

    suspend fun computeOptimizedRouteDirectionsAPI(route: Route, callback: ICallbackAPICalls)

    suspend fun computeDistanceBetweenAllPackages(
        packages: List<Package>,
        callback: ICallbackAPICalls
    )

    suspend fun computeDistanceBetweenStartLocationAndPackages(
        startLocation: Location,
        endLocation: Location,
        packages: List<Package>,
        callback: ICallbackAPICalls
    )

    suspend fun computeDistanceBetweenEndLocationAndPackages(
        endLocation: Location,
        packages: List<Package>,
        callback: ICallbackAPICalls
    )

    suspend fun computeDirectionsAPIResponse(route: Route, callback: ICallbackDirectionsResponse)
}

@Singleton
class PackagesAndAtlasRepositoryImpl(
    private val dataSource: IFirebaseRemoteDatabase,
    private val directionsApiService: DirectionsApiService,
    private val distanceMatrixApiService: DistanceMatrixApiService
) : IPackagesAndAtlasRepository {

    private lateinit var travelTimeArray: Array<IntArray>
    private lateinit var startTravelTimeArray: IntArray
    private lateinit var endTravelTimeArray: IntArray

    private lateinit var distanceArray: Array<IntArray>
    private lateinit var startDistanceArray: IntArray
    private lateinit var endDistanceArray: IntArray

    private lateinit var globalPackagesList: List<Package>
    private lateinit var globalStartLocation: Location
    private lateinit var globalEndLocation: Location

    private var finishedCalls = 0
    private var totalCalls = 0
    private lateinit var globalCallback: ICallbackAPICalls
    private lateinit var globalResponseCallback: ICallbackDirectionsResponse

    override suspend fun getDeliveryManPackages(deliveryManId: String): List<Package>? {
        var packages: List<Package>? = null

        dataSource.getDeliveryManPackages(deliveryManId).collect { state ->
            when (state) {
                is CallbackState.Loading -> {
                    println("Wait! It's loading")
                }

                is CallbackState.Success -> {
                    packages = state.data
                }

                is CallbackState.Failed -> println("Failed! ${state.message}")
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

    private fun initializeDistanceArray(size: Int) {
        distanceArray = Array(size) { IntArray(size) }
    }

    private fun initializeStartDistanceArray(size: Int) {
        startDistanceArray = IntArray(size)
    }

    private fun initializeEndDistanceArray(size: Int) {
        endDistanceArray = IntArray(size)
    }

    override suspend fun addPackage(packge: Package) { //TODO fix database connections
        dataSource.addPackage(packge).collect { state ->
            when (state) {
                is CallbackState.Loading -> {
                }

                is CallbackState.Success -> {
                    println("Package Added")
                }

                is CallbackState.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
        }
    }

    override suspend fun computeOptimizedRouteDirectionsAPI(
        route: Route,
        callback: ICallbackAPICalls
    ) {

        globalCallback = callback

        val oAddress = getFormattedAddress(route.startLocation)
        val dAddress = getFormattedAddress(route.endLocation)
        val routeLocations = getLocationsFromPackages(route.packages)
        var waypoints = getFormattedWaypointsByAddress(routeLocations)
        waypoints =
            "optimize:true|$waypoints" //TODO esto no deuria ser així. Deuria estar en la pròpia crida a la API

        val optimizedRouteResponse =
            directionsApiService.getDirectionsAPIRoute(oAddress, dAddress, waypoints)

        var totalTravelTime = 0
        var totalDistance = 0
        val sortedList = arrayListOf<Package>()
        val sortedOrder = optimizedRouteResponse.routes.last().waypoint_order

        optimizedRouteResponse.routes.forEach { optimizedRoute -> //usually it will be just one element tho
            optimizedRoute.legs.forEach { leg ->
                totalTravelTime += leg.duration.value
                totalDistance += leg.distance.value
            }
        }

        for (i in route.packages.indices) {
            sortedList.add(route.packages[sortedOrder[i]])
        }

        globalCallback.onSuccessOptimizedDirectionsAPI(
            route.copy(
                packages = sortedList,
                totalTime = totalTravelTime,
                totalDistance = totalDistance
            )
        )
    }

    override suspend fun computeDirectionsAPIResponse(
        route: Route,
        callback: ICallbackDirectionsResponse
    ) {

        globalResponseCallback = callback

        val oAddress = getFormattedAddress(route.startLocation)
        val dAddress = getFormattedAddress(route.endLocation)
        val routeLocations = getLocationsFromPackages(route.packages)
        val waypoints = getFormattedWaypointsByAddress(routeLocations)

        val directionsResponse =
            directionsApiService.getDirectionsAPIRoute(oAddress, dAddress, waypoints)

        globalResponseCallback.onSuccessResponseDirectionsAPI(directionsResponse)
    }

    private suspend fun enqueuePackagesLeg(originPackage: Package, destinationPackage: Package) {

        getLeg(originPackage.location, destinationPackage.location).collect { state ->
            when (state) {
                is CallbackState.Loading -> {
                }

                is CallbackState.Success -> {
                    //here we already have the data
                    travelTimeArray[originPackage.position][destinationPackage.position] =
                        state.data.duration!!

                    distanceArray[originPackage.position][destinationPackage.position] =
                        state.data.distance!!

                    synchronized(this/*we want to block the thread. Doesn't mind the variable we put here*/) {
                        finishedCalls++
                        if (finishedCalls == totalCalls) {
                            globalCallback.onSuccessBetweenPackages(travelTimeArray, distanceArray)
                        }
                    }

                }

                is CallbackState.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
            //if I place here the synchronous block of code, there is some problem. I have to place in the State.Success case
        }
    }

    private suspend fun enqueueStartLeg(originLocation: Location, destinationPackage: Package) {

        getLeg(originLocation, destinationPackage.location).collect { state ->
            when (state) {
                is CallbackState.Loading -> {
                }

                is CallbackState.Success -> {
                    //we already have our data
                    startTravelTimeArray[destinationPackage.position] = state.data.duration!!
                    startDistanceArray[destinationPackage.position] = state.data.distance!!

                    synchronized(this/*we want to block the thread. Doesn't mind the variable we put here*/) {
                        finishedCalls++
                        if (finishedCalls == totalCalls) {
                            globalCallback.onSuccessBetweenStartLocationAndPackages(
                                startTravelTimeArray,
                                startDistanceArray,
                                globalEndLocation,
                                globalPackagesList
                            )
                        }
                    }

                }

                is CallbackState.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
            //if I place here the synchronous block of code, there is some problem. I have to place in the State.Success case
        }
    }


    private suspend fun enqueueEndLeg(originPackage: Package, destinationLocation: Location) {
        getLeg(originPackage.location, destinationLocation).collect { state ->
            when (state) {
                is CallbackState.Loading -> {
                }

                is CallbackState.Success -> {
                    //we already have our data
                    endTravelTimeArray[originPackage.position] = state.data.duration!!
                    endDistanceArray[originPackage.position] = state.data.distance!!

                    synchronized(this/*we want to block the thread. Doesn't mind the variable we put here*/) {
                        finishedCalls++
                        if (finishedCalls == totalCalls) {
                            globalCallback.onSuccessBetweenEndLocationAndPackages(
                                endTravelTimeArray, endDistanceArray, globalPackagesList
                            )
                        }
                    }

                }

                is CallbackState.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
            //if I place here the synchronous block of code, there is some problem. I have to place in the State.Success case
        }
    }

    private suspend fun getLeg(origin: Location, destination: Location): Flow<CallbackState<Leg>> {
        return flow {
            emit(CallbackState.loading())

            val oLatLong = getStringLatLongFromLocation(origin)
            val dLatLong = getStringLatLongFromLocation(destination)

            val distanceAndTime = distanceMatrixApiService.getDistanceAndTime(oLatLong, dLatLong)

            val leg = Leg(
                distanceAndTime.rows[0].elements[0].distance.value,
                distanceAndTime.rows[0].elements[0].duration.value
            )
            emit(CallbackState.success(leg))

        }.catch {
            // If exception is thrown, emit failed state along with message.
            emit(CallbackState.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun computeDistanceBetweenAllPackages(
        packages: List<Package>,
        callback: ICallbackAPICalls
    ) {
        globalCallback = callback
        globalPackagesList = packages

        totalCalls = packages.size * packages.size
        finishedCalls = 0
        initializeTravelTimeArray(packages.size)
        initializeDistanceArray(packages.size)

        for (origin in packages) {
            for (destination in packages) {
                if (origin.numPackage != destination.numPackage) {
                    enqueuePackagesLeg(origin, destination)
                } else {
                    synchronized(this) {
                        finishedCalls++
                        if (finishedCalls == totalCalls) {
                            globalCallback.onSuccessBetweenPackages(travelTimeArray, distanceArray)
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
        callback: ICallbackAPICalls
    ) {
        globalCallback = callback
        globalPackagesList = packages
        globalStartLocation = startLocation
        globalEndLocation = endLocation

        totalCalls = packages.size
        finishedCalls = 0

        initializeStartTravelTimeArray(packages.size)
        initializeStartDistanceArray(packages.size)

        for (destination in packages) {
            enqueueStartLeg(startLocation, destination)
        }
    }

    override suspend fun computeDistanceBetweenEndLocationAndPackages(
        endLocation: Location,
        packages: List<Package>,
        callback: ICallbackAPICalls
    ) {
        globalCallback = callback
        globalPackagesList = packages
        globalEndLocation = endLocation

        totalCalls = packages.size
        finishedCalls = 0
        initializeEndTravelTimeArray(packages.size)
        initializeEndDistanceArray(packages.size)

        for (origin in packages) {
            enqueueEndLeg(origin, endLocation)
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