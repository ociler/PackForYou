package com.packforyou.data.models


data class Route(
    val id: Int = 0,
    var packages: List<Package> = listOf(),
    var totalTime: Int? = null,
    var totalDistance: Int? = null,
    var startLocation: Location, //this we be changing bc of the currentLocation of the deliveryMan
    var endLocation: Location
)