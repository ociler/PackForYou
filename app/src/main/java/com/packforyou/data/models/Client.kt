package com.packforyou.data.models

data class Client (
    override var id: String,
    override var name: String?,
    override var phone: Int?
): Person(id, name, phone)