package com.packforyou.data.repositories

import com.packforyou.api.DirectionsApiService
import com.packforyou.api.DistanceMatrixApiService
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.models.Location
import com.packforyou.data.models.State
import com.packforyou.data.models.Package
import com.packforyou.data.models.Step


interface IPackagesRepository {
    suspend fun getDeliveryManPackages(deliveryManId: String): List<Package>?
    suspend fun addPackage(packge: Package)
    suspend fun getOptimizedRoute(packages: List<Package>): List<Package>
    suspend fun getStep(origin: Location, destination: Location): Step
}
class PackagesRepositoryImpl(
    private val dataSource: IFirebaseRemoteDatabase,
    private val directionsApiService: DirectionsApiService,
    private val distanceMatrixApiService: DistanceMatrixApiService
): IPackagesRepository {

    override suspend fun getDeliveryManPackages(deliveryManId: String): List<Package>?{
        var packages: List<Package>? = null

        dataSource.getDeliveryManPackages(deliveryManId).collect { state->
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
        dataSource.addPackage(packge).collect {state ->
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

    override suspend fun getOptimizedRoute(packages: List<Package>): List<Package>{
        //TODO cal posar ací tota la lògica de json a package


        return listOf()
    }

    override suspend fun getStep(origin: Location, destination:Location): Step {
        val oLatLong = "${origin.latitude},${origin.longitude}"
        val dLatLong = "${destination.latitude},${destination.longitude}"

        val distanceAndTime = distanceMatrixApiService.getDistance(oLatLong, dLatLong)
        println("$distanceAndTime es estoooooooo")
        
        val step = Step(distanceAndTime.rows[0].elements[0].distance.value, distanceAndTime.rows[0].elements[0].duration.value)

        println(step)
        return step
    }

}