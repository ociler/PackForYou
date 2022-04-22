package com.packforyou.data.models

import java.util.*

data class Package(
    val numPackage: Int = 0,
    var isDelivered: Boolean? = false,
    var deliveryDate: Date? = null,
    var note: String? = "",
    var urgency: Urgency? = Urgency.NOT_URGENT
) {
}