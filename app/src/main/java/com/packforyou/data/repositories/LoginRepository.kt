package com.packforyou.data.repositories

import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl


interface ILoginRepository {
    fun getAllDeliveryMen(): List<DeliveryMan>

}

class LoginRepositoryImpl(

) : ILoginRepository{

    override fun getAllDeliveryMen(): List<DeliveryMan> {
        return FirebaseRemoteDatabaseImpl().getAllDeliveryMen()
    }



}
