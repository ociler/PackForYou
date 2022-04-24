package com.packforyou.data.models

import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.repositories.PackagesRepositoryImpl

data class DeliveryMan (
    override var id: String = "12345678A",
    override var name: String? = "Rick Astley",
    override var phone: Int? = 666666666,
    var mail: String? = "rick@packforyou.es",
    var password: String? = "password",
    var location: Location? = null
    ): Person(id, name, phone){
        var packages: List<Package> = listOf()
        var route: Route? = null
    }