package com.packforyou.ui.packages

import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route

interface ICallbackAPICalls {
    fun onSuccessBetweenPackages(computedTravelTimeArray: Array<IntArray>)
    fun onSuccessBetweenStartLocationAndPackages(computedTravelTimeArray: IntArray, endLocation: Location, packages: List<Package>)
    fun onSuccessBetweenEndLocationAndPackages(computedTravelTimeArray: IntArray, packages: List<Package>)
    fun onSuccessDirectionsAPI(route: Route)
}