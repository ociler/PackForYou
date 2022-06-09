package com.packforyou.data.models

abstract class Person(
    @Transient open var id: String = "12345678A",
    @Transient open var name: String? = null,
    @Transient open var phone: Int? = null
)