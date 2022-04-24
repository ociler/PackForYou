package com.packforyou.data.models

data class Client (
    override var id: String,
    override var name: String?,
    override var phone: Int?,
    var message: Message? = null,
): Person(id, name, phone)