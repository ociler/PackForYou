package com.packforyou.api

import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route

interface ICallbackAPICalls {
    fun onSuccessBetweenPackages(computedTravelTimeArray: Array<IntArray>, computedDistanceArray: Array<IntArray>)
    fun onSuccessBetweenStartLocationAndPackages(computedStartTravelTimeArray: IntArray, computedStartDistanceArray: IntArray, endLocation: Location, packages: List<Package>)
    fun onSuccessBetweenEndLocationAndPackages(computedEndTravelTimeArray: IntArray, computedEndDistanceArray: IntArray, packages: List<Package>)
    fun onSuccessOptimizedDirectionsAPI(route: Route)
    fun onSuccessNotUrgentPackages(optimizedNotUrgentRoute:Route, callbackObject: ICallbackAPICalls)
    fun onSuccessUrgentPackages(optimizedUrgentRoute:Route, callbackObject: ICallbackAPICalls)
    fun onSuccessVeryUrgentPackages(optimizedVeryUrgentRoute:Route)
}