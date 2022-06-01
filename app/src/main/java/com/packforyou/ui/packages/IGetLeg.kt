package com.packforyou.ui.packages

import com.packforyou.data.models.Location
import com.packforyou.data.models.Package

interface IGetLeg {
    fun onSuccessBetweenPackages(computedTravelTimeArray: Array<IntArray>)
    fun onSuccessBetweenStartLocationAndPackages(computedTravelTimeArray: IntArray, endLocation: Location, packages: List<Package>)
    fun onSuccessBetweenEndLocationAndPackages(computedTravelTimeArray: IntArray, packages: List<Package>)
}