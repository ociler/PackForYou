package com.packforyou.api

import com.packforyou.data.directionsDataClases.DirectionsResponse

interface ICallbackDirectionsResponse {
    fun onSuccessResponseDirectionsAPI(response: DirectionsResponse)
}