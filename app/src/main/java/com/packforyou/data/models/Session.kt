package com.packforyou.data.models

data class Session(
    var isAuthenticated: Boolean = true,
    var deliveryMan: DeliveryMan? = null
){
    fun getCurrentDeliveryMan() = deliveryMan

    fun logOut() {
        this.isAuthenticated = false
        this.deliveryMan = null
    }
}
