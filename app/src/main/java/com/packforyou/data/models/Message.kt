package com.packforyou.data.models

data class Message(
    val id: Int = 0,
    var isAtHome: Boolean? = true,
    var deliveryLocation: String = "Valencia",
    var isNextDayDelivery: Boolean? = false,
    var packageNote: String? = ""
) {
}