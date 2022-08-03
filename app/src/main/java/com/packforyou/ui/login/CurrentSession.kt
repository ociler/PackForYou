package com.packforyou.ui.login

import androidx.compose.runtime.MutableState
import com.packforyou.data.models.*

object CurrentSession {
    var userUID = ""
    var deliveryMan: DeliveryMan? = null
    var algorithm = Algorithm.NOT_ALGORITHM
    lateinit var route: MutableState<Route>
    lateinit var packagesForToday: MutableState<List<Package>>
    lateinit var packagesToDeliver: MutableState<List<Package>>
    lateinit var lastLocationsList: MutableState<List<Location>>
    lateinit var travelTime: MutableState<Int>
}