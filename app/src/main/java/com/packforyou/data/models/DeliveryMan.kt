package com.packforyou.data.models

data class DeliveryMan (
    override var id: String = "12345678A",
    override var name: String = "Rick Astley",
    override var phone: Int? = 666666666,
    var mail: String? = "rick@packforyou.es",
    var password: String? = "password",
    var currentLocation: Location? = null,
    var endLocation: Location? = null
    ): Person(id, name, phone){
        var packages: List<Package> = listOf()
        var route: Route? = null
    }