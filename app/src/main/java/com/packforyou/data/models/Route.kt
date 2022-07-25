package com.packforyou.data.models

data class Route(
    val id: Int = 0,
    var packages: List<Package> = listOf(),
    var deliveryMan: DeliveryMan? = null,
    var totalTime: Int? = null,
    var totalDistance: Int? = null
)