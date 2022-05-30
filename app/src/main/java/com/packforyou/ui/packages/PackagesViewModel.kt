package com.packforyou.ui.packages

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.packforyou.data.models.*
import com.packforyou.data.repositories.IPackagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject


interface IPackagesViewModel {
    fun addPackage(packge: Package)
    fun getOptimizedRouteMaps(route: Route): Route
    fun getAddressFromLocation(geoPoint: GeoPoint, context: Context): String
    fun getOptimizedRouteBruteForce(route: Route, travelTimeArray: Array<IntArray>): Route
    fun computeDistanceBetweenAllPackages(packages: List<Package>)
    fun observeTravelTimeArray(): LiveData<Array<IntArray>>
}

@HiltViewModel
class PackagesViewModelImpl @Inject constructor(
    private val repository: IPackagesRepository
) : ViewModel(), IPackagesViewModel {

    private val permutationsList = ArrayList<Array<Int>>()

    private var travelTimeArray = MutableLiveData<Array<IntArray>>()

    private val getLegObject = object: IGetLeg{
        override fun onSuccess(computedTravelTimeArray: Array<IntArray>) {
            travelTimeArray.postValue(computedTravelTimeArray)
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

    override fun observeTravelTimeArray(): LiveData<Array<IntArray>> {
        return travelTimeArray
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

    override fun computeDistanceBetweenAllPackages(packages: List<Package>) {
        //initializeTravelTimeArray(packages.size + 1)
        viewModelScope.launch {
            repository.computeDistanceBetweenAllPackages(packages, getLegObject)
        }
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