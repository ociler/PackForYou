package com.packforyou.data.models

data class Route(
    val id: Int = 0,
    var packages: List<Package>? = null,
    var deliveryMan: DeliveryMan? = null
)