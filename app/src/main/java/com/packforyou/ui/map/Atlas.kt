package com.packforyou.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.packforyou.data.models.Location

@Composable
fun Atlas(atlasViewModel: IAtlasViewModel) {

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
    var latLong:LatLng

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstLocation, 12f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        locations.forEach { location ->
            latLong = LatLng(location.latitude, location.longitude)
            Marker(
                state = MarkerState(position = latLong),
                title = location.address,
                snippet = "Marker in ${location.city}"
            )
        }

    }
}