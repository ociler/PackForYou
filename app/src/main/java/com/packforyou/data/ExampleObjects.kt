package com.packforyou.data

import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.models.*
import com.packforyou.data.repositories.LoginRepositoryImpl
import com.packforyou.data.repositories.PackagesRepositoryImpl
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.packages.PackagesViewModelImpl
import java.util.*

class ExampleObjects {

    var a = LoginViewModelImpl(LoginRepositoryImpl(FirebaseRemoteDatabaseImpl()))
    var b = PackagesViewModelImpl(PackagesRepositoryImpl(FirebaseRemoteDatabaseImpl()))


    val message = Message(
        id = 1,
        deliveryLocation = "",
        isAtHome = true,
        isNextDayDelivery = false,
        packageNote = null
    )

    val location1 = Location(
        address = "Plaza España",
        city = "Valencia",
        latitude = 4512.0,
        longitude = 45876.0,
        zipCode = 46020
    )

    val client1 = Client(
        id = "265478964A",
        name = "Jose Francisco Garcia",
        phone = 23456789,
        message = message
    )

    val deliveryMan1 = DeliveryMan(
        id = "789456123M",
        name = "Pedro Gómez",
        phone = 632569874,
        mail = "pedro@gmail.com",
        password = "password",
        location = location1
    )

    val package1 = Package(
        numPackage = 12,
        isDelivered = false,
        deliveryDate = Date(System.currentTimeMillis()),
        note = "Garcias",
        urgency = Urgency.NOT_URGENT,
        client = client1,
        location = location1,
        deliveryMan = deliveryMan1
    )


    val route1 = Route(
        id = 1,
        packages = listOf(package1, package1),
        deliveryMan = deliveryMan1
    )

    fun addExampleDeliveryMan(){
        a.addDeliveryMan(deliveryMan1)
    }

    fun addExamplePackage(){
        b.addPackage(package1)
    }
}