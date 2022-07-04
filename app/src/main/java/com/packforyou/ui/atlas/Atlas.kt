package com.packforyou.ui.atlas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import kotlinx.coroutines.launch
import com.packforyou.R
import com.packforyou.data.models.PackageState

@Composable
fun Atlas(atlasViewModel: IAtlasViewModel, route: Route) {
    AtlasWithGivenRoute(route, atlasViewModel)
    //CasetaAtlas()
}

@Composable
fun CasetaAtlas() {
    val caseta = LatLng(39.485749, -0.3563635)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(caseta, 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = caseta),
            title = "Caseta",
            snippet = "Marker in caseta"
        )
    }
}

@Composable
fun AtlasWithGivenLocations(locations: List<Location>) {
    val firstLocation = LatLng(locations[0].latitude, locations[0].longitude)
    var latLong: LatLng

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstLocation, 12f)
    }
    val latLongList = arrayListOf<LatLng>()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        locations.forEachIndexed { index, location ->
            latLong = LatLng(location.latitude, location.longitude)
            latLongList.add(latLong)

            when (index) {
                0 -> {
                    Marker(
                        state = MarkerState(position = latLong),
                        title = location.address,
                        snippet = "Start Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }

                locations.lastIndex -> {
                    Marker(
                        state = MarkerState(position = latLong),
                        title = location.address,
                        snippet = "End Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                }
                else -> {
                    Marker(
                        state = MarkerState(position = latLong),
                        title = location.address,
                        snippet = "Marker in ${location.city}"
                    )
                }
            }

        }
        Polyline(
            points = latLongList,
            startCap = RoundCap(),
            endCap = SquareCap()
        )
    }
}


@Composable
fun AtlasWithGivenStartEndAndPackages(
    startLocation: Location,
    endLocation: Location,
    packages: List<Package>
) {
    val firstLocation =
        LatLng(packages[0].location.latitude, packages[0].location.longitude)
    val lastLocation =
        LatLng(packages.last().location.latitude, packages.last().location.longitude)
    var latLong: LatLng

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstLocation, 12f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = firstLocation),
            title = startLocation.address,
            snippet = "Start Location",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)

        )


        Marker(
            state = MarkerState(position = lastLocation),
            title = endLocation.address,
            snippet = "End Location",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )

        packages.forEach { pckg ->
            latLong = LatLng(pckg.location.latitude, pckg.location.longitude)
            Marker(
                state = MarkerState(position = latLong),
                title = pckg.location.address,
                snippet = "Package num. ${pckg.numPackage}"
            )
        }

    }
}

@Composable
fun MapMarker(
    context: Context,
    position: LatLng,
    title: String,
    snippet: String,
    @DrawableRes iconResourceId: Int
) {
    val icon = bitmapDescriptorFromVector(
        context, iconResourceId
    )
    Marker(
        state = MarkerState(position),
        title = title,
        snippet = snippet,
        icon = icon,
    )
}

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AtlasWithGivenRoute(route: Route, viewModel: IAtlasViewModel) {
    if (route.packages == null || route.deliveryMan == null) return

    var firstLocationIfNotCurrent: Location? = null
    var endLocation: Location? = null


    var latLong: LatLng

    //if there is no currentLocation, we will get the first package as start location.
    if (route.deliveryMan!!.currentLocation == null) {
        firstLocationIfNotCurrent = route.packages!![0].location
    }

    //we use this to set the camera
    val startLocation = firstLocationIfNotCurrent ?: route.deliveryMan!!.currentLocation

    latLong = LatLng(startLocation!!.latitude, startLocation.longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLong, 12f)
    }

    //we add the endLocation in case it exists
    endLocation = route.deliveryMan!!.endLocation!!

    //We observe this pointsList, that is a mutable data that will change when we get the response of DirectionsAPI
    val pointsList by viewModel.observePointsList().observeAsState(emptyList())

    val scope = rememberCoroutineScope()

    var markerIcon = 0

    scope.launch {
        viewModel.computeDirectionsAPIResponse(route)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = MapStyleOptions(viewModel.getMapStyleString())
        )

    ) {
        route.packages!!.forEachIndexed { index, pckg ->
            val location = pckg.location
            latLong = LatLng(location.latitude, location.longitude)

            if (index == 0) { //we want to set the startLocation icon
                if (firstLocationIfNotCurrent == null) { //if currentLocation exists
                    val currentLocation = route.deliveryMan!!.currentLocation
                    latLong = LatLng(currentLocation!!.latitude, currentLocation.longitude)
                    Marker(
                        state = MarkerState(position = latLong),
                        title = location.address,
                        snippet = "Start Location",
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_location)
                    )
                    //we set the icon on currentLocation and then we go to packages[0]

                } else { //there is no currentLocation, so our startLocation is the first package
                    Marker(
                        state = MarkerState(position = latLong),
                        title = location.address,
                        snippet = "Start Location",
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_location)
                    )
                    //and then we continue to the next iteration.
                    return@forEachIndexed
                }
            }

            markerIcon = getProperMarker(pckg.state)
            MarkerInfoWindow(
                state = MarkerState(position = latLong),
                title = location.address,
                snippet = "Stop number: $index",
                icon = BitmapDescriptorFactory.fromResource(markerIcon)
            ) {
                Column {
                    Text(it.title ?: "Default Marker Title", color = Color.Blue)
                    Text(it.snippet ?: "Default Marker Snippet", color = Color.Red)
                } 
            }
        }

        Polyline(
            points = pointsList,
            color = Color.Blue
        )

    }
}

private fun getProperMarker(state: PackageState): Int {
    return when (state) {

        PackageState.CONFIRMED -> {
            R.drawable.ic_confirmed_marker
        }

        PackageState.NEW_LOCATION -> {
            R.drawable.ic_new_location_marker
        }
        else -> {
            R.drawable.ic_not_confirmed_marker
        }
    }

    //TODO we should add some markers to define the Urgency of the package
}

