package com.packforyou.data.models

data class Route(
    val id: Int = 0,
    var packages: List<Package>? = null,
    var deliveryMan: DeliveryMan? = null,
    var distancesMap: HashMap<Int, HashMap<Int, Int>>? = null //distancesMap[origin][destination] = duration
)