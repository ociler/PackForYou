package com.packforyou.data.models

import java.util.*

data class Package(
    var numPackage: Int = 0,
    var order: Int? = 0,
    var isDelivered: Boolean? = false,
    var deliveryDate: Date? = null,
    var note: String? = "",
    var urgency: Urgency? = Urgency.NOT_URGENT,
    var client: Client = Client(),
    var location: Location = Location(),
    var deliveryMan: DeliveryMan? = null,
    var message: Message? = null,
    var state: PackageState = PackageState.MESSAGE_NOT_SENT
)