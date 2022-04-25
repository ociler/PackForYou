package com.packforyou.data.repositories

import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.models.Location
import com.packforyou.data.models.State
import com.packforyou.data.models.Package
import kotlinx.coroutines.flow.collect


interface IPackagesRepository {
    suspend fun getDeliveryManPackages(deliveryManId: String): List<Package>?
    suspend fun addPackage(packge: Package)
}
class PackagesRepositoryImpl(
    private val dataSource: IFirebaseRemoteDatabase
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

}