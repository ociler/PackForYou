package com.packforyou.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.models.Client
import com.packforyou.data.models.CallbackState


interface IUsersRepository {
    suspend fun getAllDeliveryMen(): List<DeliveryMan>

    suspend fun addDeliveryMan(deliveryMan: DeliveryMan)
    suspend fun addClient(client: Client)
    suspend fun getDeliveryMan(uid: String): DeliveryMan
    fun getFirebaseAuthConnection(): FirebaseAuth

}

class UsersRepositoryImpl(
    private val dataSource: IFirebaseRemoteDatabase
) : IUsersRepository {

    override suspend fun getAllDeliveryMen(): List<DeliveryMan> {
        var deliveryMen = listOf<DeliveryMan>()

        dataSource.getAllDeliveryMen().collect { state ->
            when (state) {
                is CallbackState.Loading -> {
                    println("Wait! It's loading")
                }

                is CallbackState.Success -> {
                    deliveryMen = state.data
                }

                is CallbackState.Failed -> println("Failed! ${state.message}")
            }
        }
        return deliveryMen
    }

    override suspend fun addDeliveryMan(deliveryMan: DeliveryMan) {
        dataSource.addDeliveryMan(deliveryMan).collect { state ->
            when (state) {
                is CallbackState.Loading -> {
                }

                is CallbackState.Success -> {
                    println("DeliveryMan Added")
                }

                is CallbackState.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
        }
    }

    override suspend fun addClient(client: Client) {
        dataSource.addClient(client).collect { state ->
            when (state) {
                is CallbackState.Loading -> {
                }

                is CallbackState.Success -> {
                    println("Client Added")
                }

                is CallbackState.Failed -> {
                    println("Failed! ${state.message}")
                }
            }
        }
    }

    override suspend fun getDeliveryMan(uid: String): DeliveryMan {
        var deliveryMan = DeliveryMan()

        dataSource.getDeliveryMan(uid).collect { state ->
            when (state) {
                is CallbackState.Loading -> {
                    println("Wait! It's loading")
                }

                is CallbackState.Success -> {
                    deliveryMan = state.data
                }

                is CallbackState.Failed -> println("Failed! ${state.message}")
            }
        }

        return deliveryMan
    }

    override fun getFirebaseAuthConnection(): FirebaseAuth {
        return dataSource.getFirebaseAuthConnection()
    }
}


