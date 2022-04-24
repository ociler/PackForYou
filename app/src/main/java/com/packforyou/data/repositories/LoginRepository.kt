package com.packforyou.data.repositories

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.models.State
import kotlinx.coroutines.flow.collect
import javax.sql.CommonDataSource


interface ILoginRepository {
    suspend fun getAllDeliveryMen(): List<DeliveryMan>

    suspend fun addDeliveryMan(deliveryMan: DeliveryMan)

}

class LoginRepositoryImpl(
    private val dataSource: IFirebaseRemoteDatabase
) : ILoginRepository {

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
}

