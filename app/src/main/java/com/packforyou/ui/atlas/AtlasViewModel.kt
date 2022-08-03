package com.packforyou.ui.atlas

import android.location.LocationManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.packforyou.api.ICallbackDirectionsResponse
import com.packforyou.data.directionsDataClases.DirectionsResponse
import com.packforyou.data.models.Algorithm
import com.packforyou.data.models.Location
import com.packforyou.data.models.Route
import com.packforyou.data.repositories.IPackagesAndAtlasRepository
import com.packforyou.ui.login.CurrentSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface IAtlasViewModel {

    suspend fun computeDirectionsAPIResponse(route: Route)
    fun observePointsList(): MutableLiveData<List<LatLng>>
    fun getMapStyleString(): String
    fun setCurrentLocation(location: Location)
}

@HiltViewModel
class AtlasViewModelImpl @Inject constructor(
    private val repository: IPackagesAndAtlasRepository
) : ViewModel(), IAtlasViewModel {

    var pointsList = MutableLiveData<List<LatLng>>()

    private val callbackObject = object : ICallbackDirectionsResponse {
        override fun onSuccessResponseDirectionsAPI(response: DirectionsResponse) {
            val encodedPoints = response.routes[0].overview_polyline.points
            var travelTime = 0

            pointsList.postValue(PolyUtil.decode(encodedPoints).toList())
            response.routes[0].legs.forEach {
                travelTime += it.duration.value
            }

            if(CurrentSession.algorithm != Algorithm.URGENCY) {
                CurrentSession.travelTime.value = travelTime
            }
        }
    }

    override suspend fun computeDirectionsAPIResponse(route: Route) {
        repository.computeDirectionsAPIResponse(route, callbackObject)
    }

    override fun observePointsList(): MutableLiveData<List<LatLng>> {
        return pointsList
    }

    override fun getMapStyleString(): String {
        val filePath = "json/map_style.json"

        return this::class.java.classLoader?.getResource(filePath)!!.readText()
    }

    override fun setCurrentLocation(location: Location) {
        val deliveryMan = CurrentSession.deliveryMan
        if (deliveryMan != null) {
            deliveryMan.currentLocation = location

            val gpsLocation = android.location.Location(LocationManager.GPS_PROVIDER)
            gpsLocation.latitude = location.latitude
            gpsLocation.longitude = location.longitude
        }
    }
}