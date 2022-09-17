package com.packforyou.data.models

import com.packforyou.data.directionsDataClases.DirectionsResponse

sealed class DirectionsState<T> {

    data class Success<T>(val data: DirectionsResponse) : DirectionsState<T>()
    data class Failed<T>(val message: String) : DirectionsState<T>()
}