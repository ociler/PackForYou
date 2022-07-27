package com.packforyou.ui.atlas

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import com.packforyou.R
import com.packforyou.data.models.*
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.packages.StateIcon
import com.packforyou.ui.theme.*

lateinit var cameraPositionState: CameraPositionState

@Composable
fun AtlasScreen(atlasViewModel: IAtlasViewModel) {
    AtlasWithMutableRoute(atlasViewModel)
}

@Composable
fun CasetaAtlas() {
    val caseta = LatLng(39.485749, -0.3563635)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(caseta, 100f)
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


@Composable
fun AtlasWithGivenRoute(route: Route, viewModel: IAtlasViewModel) {

    var latLong: LatLng


    //we use this to set the camera
    val firstLocation = if (route.startLocation != null)
        route.startLocation
    else route.packages[0].location

    latLong = LatLng(firstLocation.latitude, firstLocation.longitude)

    cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLong, 16.5f)
    }


    val endLocation = route.endLocation

    //We observe this pointsList, that is a mutable data that will change when we get the response of DirectionsAPI
    val pointsList by viewModel.observePointsList().observeAsState(emptyList())

    val scope = rememberCoroutineScope()

    var markerIcon: Int

    //this way we get the Polyline to draw the path
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

        val currentLocation = route.startLocation
        latLong = LatLng(currentLocation.latitude, currentLocation.longitude)
        MarkerInfoWindow(
            state = MarkerState(position = latLong),
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_location)
        ) {
            CustomStartMarkerWindow(startLocation = currentLocation)
        }
    }

    //We place all the markers
    route.packages.forEachIndexed { index, pckg ->
        val location = pckg.location
        latLong = LatLng(location.latitude, location.longitude)

        markerIcon = getProperMarker(pckg.state)
        MarkerInfoWindow(
            state = MarkerState(position = latLong),
            title = location.address,
            snippet = "Stop number: $index",
            icon = BitmapDescriptorFactory.fromResource(markerIcon)
        ) {
            CustomMarkerInfoWindow(pckg)
        }
    }

    latLong = LatLng(endLocation.latitude, endLocation.longitude)
    MarkerInfoWindow( //TODO change finish icon
        state = MarkerState(position = latLong),
        icon = BitmapDescriptorFactory.fromResource(R.drawable.finish)
    ) {
        CustomEndMarkerWindow(endLocation = endLocation)
    }


    Polyline(
        points = pointsList,
        color = Color.Black
    )

}


@Composable
fun AtlasWithMutableRoute(viewModel: IAtlasViewModel) {
    val route = CurrentSession.route

    var latLong: LatLng


    latLong = LatLng(route.value.startLocation.latitude, route.value.startLocation.longitude)

    cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLong, 16.5f)
    }

    //we add the endLocation in case it exists
    val endLocation = route.value.endLocation

    //We observe this pointsList, that is a mutable data that will change when we get the response of DirectionsAPI
    val pointsList by viewModel.observePointsList().observeAsState(emptyList())

    val scope = rememberCoroutineScope()

    var markerIcon: Int

    //this way we get the Polyline to draw the path
    scope.launch {
            viewModel.computeDirectionsAPIResponse(route.value)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = MapStyleOptions(viewModel.getMapStyleString())
        )
    ) {
        val startLocation = route.value.startLocation

        latLong = LatLng(startLocation.latitude, startLocation.longitude)
        MarkerInfoWindow(
            state = MarkerState(position = latLong),
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_location)
        ) {
            CustomStartMarkerWindow(startLocation = startLocation)
        }


        //We place all the markers
        route.value.packages.forEachIndexed { index, pckg ->
            val location = pckg.location
            latLong = LatLng(location.latitude, location.longitude)

            markerIcon = getProperMarker(pckg.state)
            MarkerInfoWindow(
                state = MarkerState(position = latLong),
                title = location.address,
                snippet = "Stop number: $index",
                icon = BitmapDescriptorFactory.fromResource(markerIcon)
            ) {
                CustomMarkerInfoWindow(pckg)
            }
        }


        //We set end location in case it exists
        if (endLocation != null) {
            latLong = LatLng(endLocation.latitude, endLocation.longitude)
            MarkerInfoWindow( //TODO change finish icon
                state = MarkerState(position = latLong),
                icon = BitmapDescriptorFactory.fromResource(R.drawable.finish)
            ) {
                CustomEndMarkerWindow(endLocation = endLocation)
            }
        }

        Polyline(
            points = pointsList,
            color = Color.Black
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
}

@Composable
fun CustomMarkerInfoWindow(pckg: Package) {
    val urgencyText = getUrgencyText(pckg.urgency)
    Surface(
        shadowElevation = 10.dp,
        color = White,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.fillMaxWidth(.6f)
    ) {
        Column(
            Modifier.padding(
                start = 15.dp, end = 5.dp, top = 10.dp, bottom = 10.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REF ${pckg.numPackage}",
                    style = PackForYouTypography.displayLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = urgencyText,
                    style = PackForYouTypography.bodyMedium,
                    fontSize = 16.sp,
                    color = getStateColor(pckg.state),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 15.dp)
                )
            }

            Text(
                text = pckg.location.address,
                style = PackForYouTypography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = pckg.client.name,
                style = PackForYouTypography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            StateIcon(
                state = pckg.state,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

        }
    }
}

@Composable
fun CustomStartMarkerWindow(startLocation: Location) {
    Surface(
        shadowElevation = 10.dp,
        color = White,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.fillMaxWidth(.5f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    start = 15.dp, end = 5.dp, top = 10.dp, bottom = 10.dp
                )
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home icon",
                tint = Black
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = startLocation.address,
                style = PackForYouTypography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CustomEndMarkerWindow(endLocation: Location) {
    Surface(
        shadowElevation = 10.dp,
        color = White,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.fillMaxWidth(.5f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    start = 15.dp, end = 5.dp, top = 10.dp, bottom = 10.dp
                )
        ) {
            Icon(
                imageVector = Icons.Filled.Home, //TODO change for end icon
                contentDescription = "End icon",
                tint = Black
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = endLocation.address,
                style = PackForYouTypography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun getUrgencyText(urgency: Urgency?): String {
    return when (urgency) {
        Urgency.VERY_URGENT -> "VERY URGENT"
        Urgency.URGENT -> "URGENT"
        else -> "STANDARD"
    }
}

private fun getStateColor(state: PackageState): Color {
    return when (state) {
        PackageState.NOT_CONFIRMED -> {
            NotConfirmed
        }
        PackageState.CONFIRMED -> {
            Confirmed
        }

        PackageState.POSTPONED_DELIVERY -> {
            Cancelled
        }

        PackageState.NEW_LOCATION -> {
            NewLocation
        }
        else -> {
            NotConfirmed
        }
    }
}

