package com.packforyou.data.models

import java.time.LocalDate

data class Package(
    var numPackage: Int = 0,
    var position: Int = 0,
    var isDelivered: Boolean = false,
    var deliveryDate: LocalDate? = null,
    var note: String? = "",
    var urgency: Urgency? = Urgency.NOT_URGENT,
    var client: Client = Client(),
    var location: Location = Location(),
    var message: Message? = null,
    var state: PackageState = PackageState.MESSAGE_NOT_SENT
)