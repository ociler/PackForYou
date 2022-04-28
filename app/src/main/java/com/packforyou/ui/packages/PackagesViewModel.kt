package com.packforyou.ui.packages

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.packforyou.data.models.Package
import com.packforyou.data.repositories.IPackagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


interface IPackagesViewModel {
    fun addPackage(packge: Package)
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

}