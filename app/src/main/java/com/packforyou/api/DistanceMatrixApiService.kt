package com.packforyou.api

import com.packforyou.BuildConfig
import com.packforyou.data.distanceMatrixDataClases.DistanceAndTime
import retrofit2.http.GET
import retrofit2.http.Query

const val MATRIX_URL = "maps/api/distancematrix/json"
const val KEY_URL_REFERENCE = "key=${BuildConfig.MAPS_API_KEY}"

interface DistanceMatrixApiService {

    @GET("$MATRIX_URL?mode=driving&language=en-EN&sensor=false&$KEY_URL_REFERENCE")
    suspend fun getDistance(
        @Query("origins") oLatLong: String,
        @Query("destinations") dLatLong: String
    ) : DistanceAndTime
}