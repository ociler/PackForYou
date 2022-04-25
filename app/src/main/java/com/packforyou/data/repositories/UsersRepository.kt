package com.packforyou.data.repositories

import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.models.Client
import com.packforyou.data.models.State
import kotlinx.coroutines.flow.collect


interface IUsersRepository {
    suspend fun getAllDeliveryMen(): List<DeliveryMan>

    suspend fun addDeliveryMan(deliveryMan: DeliveryMan)
    suspend fun addClient(client: Client)

}

class UsersRepositoryImpl(
    private val dataSource: IFirebaseRemoteDatabase
) : IUsersRepository {

    override suspend fun getAllDeliveryMen(): List<DeliveryMan> {
        var deliveryMen = listOf<DeliveryMan>()

        dataSource.getAllDeliveryMen().collect { state->
            when (state) {
                is State.Loading -> {
                    println("Wait! It's loading")
                }

                is State.Success -> {
                   deliveryMen = state.data
                }

                is State.Failed -> println("Failed! ${state.message}")
            }
        }
        return deliveryMen
    }

    override suspend fun addDeliveryMan(deliveryMan: DeliveryMan) {
        dataSource.addDeliveryMan(deliveryMan).collect { state ->
            when (state) {
                is State.Loading -> {
                }

                is State.Success -> {
                    println("DeliveryMan Added")
                }

                is State.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
        }
    }

    override suspend fun addClient(client: Client) {
        dataSource.addClient(client).collect { state ->
            when (state) {
                is State.Loading -> {
                }

                is State.Success -> {
                    println("Client Added")
                }

                is State.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
        }
    }
}

