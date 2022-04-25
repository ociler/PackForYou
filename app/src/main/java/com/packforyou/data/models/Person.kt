package com.packforyou.data.models

abstract class Person(
    open var id: String = "12345678A",
    open var name: String? = null,
    open var phone: Int? = null
) {

}