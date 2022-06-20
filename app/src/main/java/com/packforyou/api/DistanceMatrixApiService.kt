package com.packforyou.api

import com.packforyou.data.distanceMatrixDataClases.DistanceAndTime
import retrofit2.http.GET
import retrofit2.http.Query

interface DistanceMatrixApiService {

    @GET("$MATRIX_URL?mode=driving&language=en-EN&sensor=false&$KEY_URL_REFERENCE")
    suspend fun getDistanceAndTime(
        @Query("origins") oLatLong: String,
        @Query("destinations") dLatLong: String
    ) : DistanceAndTime
}