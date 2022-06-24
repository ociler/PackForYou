package com.packforyou.data.models

data class Client (
    override var id: String = "123456789A",
    override var name: String = "John Doe",
    override var phone: Int? = 654654654
): Person(id, name, phone)