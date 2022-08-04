package com.packforyou.data

import com.packforyou.data.models.*
import com.packforyou.ui.login.ILoginViewModel
import com.packforyou.ui.packages.IPackagesViewModel
import java.time.LocalDate
import java.util.*

class ExampleObjects(
    private val packagesViewModel: IPackagesViewModel,
    private val loginViewModel: ILoginViewModel
) {

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

    val location2 = Location(
        address = "Plaça Catalunya",
        city = "Barcelona",
        latitude = 4512.0,
        longitude = 45876.0,
        zipCode = 2547
    )

    val client1 = Client(
        id = "265478964AB",
        name = "Jose Francisco Garcia",
        phone = 23456789
    )

    val deliveryMan1 = DeliveryMan(
        id = "789456124M",
        name = "Pedro Gómez",
        phone = 632569874,
        mail = "pedro@gmail.com",
        password = "password",
        currentLocation = location1
    )

    val deliveryMan2 = DeliveryMan(
        id = "12345678A",
        name = "Paco Gómez",
        phone = 659874123,
        mail = "paco@gmail.com",
        password = "password"
    )

    val package1 = Package(
        numPackage = 12,
        isDelivered = false,
        deliveryDate = LocalDate.now(),
        note = "Garcias",
        urgency = Urgency.NOT_URGENT,
        client = client1,
        location = location1,
        message = message
    )


    val route1 = Route(
        id = 1,
        packages = listOf(package1, package1),
        startLocation = deliveryMan1.currentLocation!!,
        endLocation = deliveryMan1.lastLocation!!
    )

    fun addExampleDeliveryMan(){
        loginViewModel.addDeliveryMan(deliveryMan1)
        loginViewModel.addDeliveryMan(deliveryMan2)
    }

    fun addExamplePackage(){
        packagesViewModel.addPackage(package1)
    }

    fun addExampleClient(){
        loginViewModel.addClient(client1)
    }


}