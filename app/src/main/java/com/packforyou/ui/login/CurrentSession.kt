package com.packforyou.ui.login

import androidx.compose.runtime.MutableState
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route

object CurrentSession {
    var userUID = ""
    var deliveryMan: DeliveryMan? = null
    lateinit var route: MutableState<Route>
    lateinit var packagesForToday: MutableState<List<Package>>
    lateinit var packagesToDeliver: MutableState<List<Package>>
    lateinit var lastLocationsList: MutableState<List<Location>>
}