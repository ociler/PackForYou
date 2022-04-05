package com.packforyou.data

data class Client (
    override var id: String,
    override var name: String,
    override var phone: Int,
    var isAtHome: Boolean,
    var deliveryLocation: String,
    var isNextDayDelivery: Boolean,
    var packageNote: String
): Person(id, name, phone) {

}