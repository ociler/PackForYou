package com.packforyou.ui.atlas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import com.packforyou.R

@Composable
fun Atlas(atlasViewModel: IAtlasViewModel, route: Route) {
    AtlasWithGivenRoute(route, atlasViewModel)
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

    val firstLocation: Location

    var latLong: LatLng
    val locations = arrayListOf<Location>()

    //if there is no currentLocation, we will get the first package. If there is, we add it to the locations list
    //we will use this list to set the pointers
    if (route.deliveryMan!!.currentLocation != null) {
        firstLocation = route.deliveryMan!!.currentLocation!!
        locations.add(firstLocation)
    } else {
        firstLocation = route.packages!![0].location
    }

    latLong = LatLng(firstLocation.latitude, firstLocation.longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLong, 12f)
    }

    route.packages!!.forEach {
        locations.add(it.location)
    }

    //and now we add the endLocation in case it exists
    if (route.deliveryMan!!.endLocation != null) {
        val endLocation = route.deliveryMan!!.endLocation!!
        locations.add(endLocation)
    }

    //We observe this pointsList, that is a mutable data that will change when we get the response of DirectionsAPI
    val pointsList by viewModel.observePointsList().observeAsState(emptyList())

    val scope = rememberCoroutineScope()

    scope.launch {
        viewModel.computeDirectionsAPIResponse(route)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        locations.forEachIndexed { index, location ->
            latLong = LatLng(location.latitude, location.longitude)

            when (index) {
                0 -> {
                    Marker(
                        state = MarkerState(position = latLong),
                        title = location.address,
                        snippet = "Start Location",
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.home)
                    )
                }

                locations.lastIndex -> {
                    Marker(
                        state = MarkerState(position = latLong),
                        title = location.address,
                        snippet = "End Location",
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.finish)
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
            points = pointsList,
            color = Color.Blue
        )

    }
}

