package com.packforyou.data

data class DeliveryMan (
    override var id: String,
    override var name: String,
    override var phone: Int,
    var mail: String,
    var password: String
    ): Person(id, name, phone) {
}