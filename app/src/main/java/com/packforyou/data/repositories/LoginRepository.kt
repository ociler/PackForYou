package com.packforyou.data.repositories

import com.packforyou.data.DeliveryMan
import com.packforyou.data.dataSources.FirebaseDatabase

class LoginRepository {
    fun getAllDeliveryMen(): List<DeliveryMan> {
        return FirebaseDatabase.getAllDeliverymen()
    }
}