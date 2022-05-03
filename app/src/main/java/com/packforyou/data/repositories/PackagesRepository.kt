package com.packforyou.data.repositories

import com.packforyou.api.DirectionsApiService
import com.packforyou.api.DistanceMatrixApiService
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.models.*


interface IPackagesRepository {
    suspend fun getDeliveryManPackages(deliveryManId: String): List<Package>?
    suspend fun addPackage(packge: Package)
    suspend fun getLeg(origin: Location, destination: Location): Leg
    suspend fun getOptimizedRoute(route: Route): Route?
}

class PackagesRepositoryImpl(
    private val dataSource: IFirebaseRemoteDatabase,
    private val directionsApiService: DirectionsApiService,
    private val distanceMatrixApiService: DistanceMatrixApiService
) : IPackagesRepository {

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
        waypoints = "optimize:true|$waypoints" //TODO esto no deuria ser així. Deuria estar en la pròpia crida a la API

        val optimizedRouteResponse =
            directionsApiService.getOptimizedRoute(oAddress, dAddress, waypoints)

        val sortedList = arrayListOf<Package>()
        val sortedOrder = optimizedRouteResponse.routes.last().waypoint_order

        for (i in 0 until route.packages!!.size) {
            sortedList.add(route.packages!![sortedOrder[i]])
        }

        return Route(route.id, sortedList, route.deliveryMan)
    }


    override suspend fun getLeg(origin: Location, destination: Location): Leg {
        val oLatLong = getStringLatLongFromLocation(origin)
        val dLatLong = getStringLatLongFromLocation(destination)

        val distanceAndTime = distanceMatrixApiService.getDistance(oLatLong, dLatLong)
        println("$distanceAndTime es estoooooooo")

        val step = Leg(
            distanceAndTime.rows[0].elements[0].distance.value,
            distanceAndTime.rows[0].elements[0].duration.value
        )

        println(step)
        return step
    }

    private fun getStringLatLongFromLocation(location: Location?): String {
        return if (location != null) {
            "${location.latitude},${location.longitude}"
        } else {
            ""
        }
    }

    private fun getFormattedAddress(location: Location?): String {
        return if (location != null) {
            location.address.replace(' ', '+')
        } else {
            ""
        }
    }

    private fun getFormattedWaypointsByLocation(locations: List<Location?>): String {
        var result = ""

        if (locations != null) {
            result = getStringLatLongFromLocation(locations[0])

            for (location in locations.subList(1, locations.size)) {
                result += "|${getStringLatLongFromLocation(location)}"
            }
        }
        return result
    }

    private fun getFormattedWaypointsByAddress(locations: List<Location?>): String {
        var result = ""

        if (locations != null) {
            result = getFormattedAddress(locations[0])

            for (location in locations.subList(1, locations.size)) {
                result += "|${getFormattedAddress(location)}"
            }
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