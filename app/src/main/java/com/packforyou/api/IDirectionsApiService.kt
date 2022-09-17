package com.packforyou.api

import com.packforyou.data.directionsDataClases.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//https://maps.googleapis.com/maps/api/directions/json?origin=1337%20India%20St%2C%20San%20Diego%2C%20CA%2092101%2C%20United%20States&destination=1485%20E%20St%2C%20San%20Diego%2C%20CA%2092101%2C%20United%20States
// &waypoints=optimize:true|1200%20Third%20Ave%2C%20San%20Diego%2C%20CA%2092101%2C%20United%20States%7C707%20Tenth%20Ave%2C%20San%20Diego%2C%20CA%2092101%2C%20United%20States%7C180%20Broadway%20Suite%20101%2C%20San%20Diego%2C%20CA%2092101%2C%20United%20States%7C945%20Broadway%2C%20San%20Diego%2C%20CA%2092101%2C%20United%20States&key=


interface IDirectionsApiService {
    @GET("$DIRECTIONS_URL?&$KEY_URL_REFERENCE") //TODO voldria fer amb path, pero no tira. Com faig per a poder posar el que vulga?
    suspend fun getDirectionsAPIRoute(
        @Query("origin") oAddress: String,
        @Query("destination") dAddress: String,
        @Query("waypoints") wayPoints: String
    ): DirectionsResponse
}
