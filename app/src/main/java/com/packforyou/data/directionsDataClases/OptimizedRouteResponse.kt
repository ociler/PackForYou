package com.packforyou.data.directionsDataClases

data class OptimizedRouteResponse(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)