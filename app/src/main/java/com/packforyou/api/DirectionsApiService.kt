package com.packforyou.api

import retrofit2.Response
import retrofit2.http.GET

interface DirectionsApiService {
    @GET("maps/api/directions/json?")
    suspend fun getCountries(): Response<Package>
}