package com.packforyou.ui.login

import androidx.compose.runtime.MutableState
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package

object CurrentSession {
    var deliveryMan: DeliveryMan? = null
    lateinit var packagesForToday: MutableState<List<Package>>
    lateinit var packagesToDeliver: MutableState<List<Package>>
    lateinit var lastLocationsList: MutableState<List<Location>>
    var firstAccess = true
}