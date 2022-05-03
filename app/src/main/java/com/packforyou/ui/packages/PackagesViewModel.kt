package com.packforyou.ui.packages

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.packforyou.data.models.Leg
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import com.packforyou.data.repositories.IPackagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject


interface IPackagesViewModel {
    fun addPackage(packge: Package)
    fun getLocationFromAddress(strAddress: String?, context: Context): GeoPoint?
    fun getLeg(origin: Location, destination: Location): Leg
    fun getOptimizedRoute(route: Route): Route
    fun getAddressFromLocation(GeoPoint: GeoPoint, context: Context): String
}

@HiltViewModel
class PackagesViewModelImpl @Inject constructor(
    private val repository: IPackagesRepository
) : ViewModel(), IPackagesViewModel {


    override fun addPackage(packge: Package) {
        viewModelScope.launch {
            repository.addPackage(packge)
        }
    }

    override fun getLocationFromAddress(strAddress: String?, context: Context): GeoPoint? {
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

    override fun getAddressFromLocation(geoPoint: GeoPoint, context: Context): String{
        val addresses: List<Address>
        val geocoder= Geocoder(context, Locale.getDefault())

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

    override fun getLeg(origin: Location, destination: Location): Leg {
        var leg = Leg(0,0)
        viewModelScope.launch {
            leg = repository.getLeg(origin, destination)
        }

        return leg
    }

    override fun getOptimizedRoute(route: Route): Route {
        var optimizedRoute = Route()
        viewModelScope.launch {
            optimizedRoute = repository.getOptimizedRoute(route)!!
        }

        return optimizedRoute
    }

}