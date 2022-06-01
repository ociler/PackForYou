package com.packforyou.ui.packages

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import com.packforyou.data.repositories.IPackagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


interface IPackagesViewModel {
    fun addPackage(packge: Package)
    fun getOptimizedRouteMaps(route: Route): Route
    fun getAddressFromLocation(geoPoint: GeoPoint, context: Context): String
    fun getOptimizedRouteBruteForce(route: Route, travelTimeArray: Array<IntArray>): Route
    fun computeDistanceBetweenAllPackages(packages: List<Package>)

    fun computeDistanceBetweenStartLocationAndPackages(
        startLocation: Location,
        endLocation: Location,
        packages: List<Package>
    )

    fun computeDistanceBetweenEndLocationAndPackages(endLocation: Location, packages: List<Package>)

    fun getOptimizedRouteClosestNeighbour(
        startLocation: Location,
        endLocation: Location,
        route: Route,
        travelTimeArray: Array<IntArray>?,
        startTravelTimeArray: IntArray,
        endTravelTimeArray: IntArray
    ): Route
}

@HiltViewModel
class PackagesViewModelImpl @Inject constructor(
    private val repository: IPackagesRepository
) : ViewModel(), IPackagesViewModel {

    private val permutationsList = ArrayList<Array<Int>>()

    var travelTimeArray = MutableLiveData<Array<IntArray>>()
    lateinit var startTravelTimeArray: IntArray
    lateinit var endTravelTimeArray: IntArray

    private val getLegObject = object : IGetLeg {
        override fun onSuccessBetweenPackages(computedTravelTimeArray: Array<IntArray>) {
            travelTimeArray.postValue(computedTravelTimeArray)
        }

        //Here we need the endLocation because we are going to call "computeDistanceBetweenEndLocationAndPackages"
        override fun onSuccessBetweenStartLocationAndPackages(computedTravelTimeArray: IntArray, endLocation: Location, packages: List<Package>) {
            startTravelTimeArray = computedTravelTimeArray
            computeDistanceBetweenEndLocationAndPackages(endLocation, packages)
        }

        override fun onSuccessBetweenEndLocationAndPackages(computedTravelTimeArray: IntArray, packages: List<Package>) {
            endTravelTimeArray = computedTravelTimeArray
            computeDistanceBetweenAllPackages(packages)
        }
    }


    override fun addPackage(packge: Package) {
        viewModelScope.launch {
            repository.addPackage(packge)
        }

    }

    fun getLocationFromAddress(strAddress: String?, context: Context): GeoPoint? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: GeoPoint? = null
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            p1 = GeoPoint(
                (location.latitude),
                (location.longitude)
            )
            return p1
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun getAddressFromLocation(geoPoint: GeoPoint, context: Context): String {
        val addresses: List<Address>
        val geocoder = Geocoder(context, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            geoPoint.latitude,
            geoPoint.longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName // Only if available else return NULL

        return address.replace(", ", ",")
    }


    override fun computeDistanceBetweenAllPackages(packages: List<Package>) {
        //initializeTravelTimeArray(packages.size + 1)
        viewModelScope.launch {
            repository.computeDistanceBetweenAllPackages(packages, getLegObject)
        }
    }

    override fun computeDistanceBetweenStartLocationAndPackages(
        startLocation: Location,
        endLocation: Location,
        packages: List<Package>
    ) {
        viewModelScope.launch {
            repository.computeDistanceBetweenStartLocationAndPackages(
                startLocation,
                endLocation,
                packages,
                getLegObject
            )
        }
    }

    override fun computeDistanceBetweenEndLocationAndPackages(
        endLocation: Location,
        packages: List<Package>
    ) {
        viewModelScope.launch {
            repository.computeDistanceBetweenEndLocationAndPackages(
                endLocation,
                packages,
                getLegObject
            )
        }
    }


    override fun getOptimizedRouteMaps(route: Route): Route {
        var optimizedRoute = Route()
        viewModelScope.launch {
            optimizedRoute = repository.getOptimizedRoute(route)!!
        }

        return optimizedRoute
    }

    override fun getOptimizedRouteBruteForce(
        route: Route,
        travelTimeArray: Array<IntArray>
    ): Route {
        if (route.packages == null) return route

        //So many API calls. It will be better to don't use this method with more than 5 packages
        if (route.packages!!.size > 5) return route

        val permutations = getPermutations(
            Array(route.packages!!.size) { it }
        )

        var minimumTotalTravelTime = Int.MAX_VALUE
        var bestPermutation = permutations[0]
        var origin: Int
        var destination: Int

        var totalTravelTime: Int
        var legTravelTime: Int

        for (permutation in permutations) { //we have one permutation
            totalTravelTime = 0

            for (permPosition in 0 until permutation.size - 1) { //we have the position we want to check in our route
                origin = route.packages!![permutation[permPosition]].numPackage
                destination = route.packages!![permutation[permPosition + 1]].numPackage

                legTravelTime = travelTimeArray[origin][destination]
                totalTravelTime += legTravelTime

            }

            if (totalTravelTime < minimumTotalTravelTime) {
                minimumTotalTravelTime = totalTravelTime
                bestPermutation = permutation
            }
        }

        val optimizedPackages = arrayListOf<Package>()
        for (i in bestPermutation) {
            optimizedPackages.add(route.packages!![i])
        }

        return route.copy(packages = optimizedPackages)

    }


    override fun getOptimizedRouteClosestNeighbour(
        startLocation: Location,
        endLocation: Location,
        route: Route,
        travelTimeArray: Array<IntArray>?,
        startTravelTimeArray: IntArray,
        endTravelTimeArray: IntArray
    ): Route {
        if (route.packages == null) return route

        val packages = route.packages!!
        var closestNeighbour = packages[0]
        var minimumTime = startTravelTimeArray[0]
        val optimizedList = ArrayList<Package>()
        var totalTravelTime = 0

        val visitedArray = BooleanArray(route.packages!!.size)

        for (i in 1 until startTravelTimeArray.size) {
            if (startTravelTimeArray[i] < minimumTime) {
                minimumTime = startTravelTimeArray[i]
                closestNeighbour = packages[i]
            }
        }

        //We have the first package we have to go from the route
        optimizedList.add(closestNeighbour)
        totalTravelTime += minimumTime
        visitedArray[closestNeighbour.numPackage] = true

        var minIndex = 0


        //Now we add all the middle packages
        while (!areAllTrue(visitedArray)) {
            minimumTime = Int.MAX_VALUE
            travelTimeArray!![closestNeighbour.numPackage].forEachIndexed { index, currentTravelTime ->
                if (!visitedArray[index] && currentTravelTime < minimumTime) {
                    minimumTime = currentTravelTime
                    minIndex = index
                }
            }

            closestNeighbour = packages[minIndex]
            optimizedList.add(closestNeighbour)
            totalTravelTime += minimumTime
            visitedArray[minIndex] = true
        }

        //And finally we add the travel time of going from this last package to the endLocation
        totalTravelTime += endTravelTimeArray[closestNeighbour.numPackage]

        return route.copy(packages = optimizedList, totalTime = totalTravelTime)

    }

    private fun areAllTrue(array: BooleanArray): Boolean {
        for (b in array) if (!b){
            return false
        }
        return true
    }

    private fun getPermutations(elements: Array<Int>): ArrayList<Array<Int>> {
        permutationsList.clear()
        computePermutationsRecursive(elements.size, elements, 'a')
        return permutationsList
    }

    private fun computePermutationsRecursive(
        n: Int, elements: Array<Int>, delimiter: Char
    ) {
        if (n == 1) {
            permutationsList.add(elements.clone())
        } else {
            for (i in 0 until n - 1) {
                computePermutationsRecursive(n - 1, elements, delimiter)
                if (n % 2 == 0) {
                    swap(elements, i, n - 1)
                } else {
                    swap(elements, 0, n - 1)
                }
            }
            computePermutationsRecursive(n - 1, elements, delimiter)
        }
    }

    private fun swap(input: Array<Int>, a: Int, b: Int) {
        val tmp: Int = input[a]
        input[a] = input[b]
        input[b] = tmp
    }

}