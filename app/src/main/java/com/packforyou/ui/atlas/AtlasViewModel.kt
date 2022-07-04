package com.packforyou.ui.atlas

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.packforyou.api.ICallbackDirectionsResponse
import com.packforyou.data.directionsDataClases.DirectionsResponse
import com.packforyou.data.models.Route
import com.packforyou.data.repositories.IPackagesAndAtlasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface IAtlasViewModel{

    suspend fun computeDirectionsAPIResponse(route: Route)
    fun observePointsList(): MutableLiveData<List<LatLng>>
    fun getMapStyleString(): String
}

@HiltViewModel
class AtlasViewModelImpl @Inject constructor(
    private val repository: IPackagesAndAtlasRepository
) : ViewModel(), IAtlasViewModel {

    var pointsList = MutableLiveData<List<LatLng>>()

    private val callbackObject = object : ICallbackDirectionsResponse {
        override fun onSuccessResponseDirectionsAPI(response: DirectionsResponse) {
            val encodedPoints = response.routes[0].overview_polyline.points
            pointsList.postValue(PolyUtil.decode(encodedPoints).toList())
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
}