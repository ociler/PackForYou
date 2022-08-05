package com.packforyou.ui.packages

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.packforyou.R
import com.packforyou.data.models.Package
import com.packforyou.navigation.Screen
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.home.AppBar
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White


val packagesList = CurrentSession.packagesToDeliver

private lateinit var currentPosition: MutableState<Int>

private lateinit var currentPackage: MutableState<Package>
private lateinit var previousPackage: MutableState<Package>
private lateinit var nextPackage: MutableState<Package>

private lateinit var markerPosition: MutableState<LatLng>
private lateinit var currentLocation: MutableState<LatLng>

private lateinit var pointsList: State<List<LatLng>>
private lateinit var cameraPositionState: CameraPositionState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartRouteScreen(
    navController: NavController,
    owner: ViewModelStoreOwner,
    fusedLocationClient: FusedLocationProviderClient
) {
    val context = LocalContext.current
    val endLocation = CurrentSession.route.value.endLocation

    val packagesViewModel =
        ViewModelProvider(owner)[PackagesViewModelImpl::class.java]

    val atlasViewModel =
        ViewModelProvider(owner)[AtlasViewModelImpl::class.java]

    currentPosition = remember {
        mutableStateOf(0)
    }

    currentPackage = remember {
        mutableStateOf(packagesList.value[0])
    }

    previousPackage = remember {
        mutableStateOf(packagesList.value[0])
    }

    nextPackage = remember {
        mutableStateOf(packagesList.value[0])
    }

    markerPosition = remember {
        mutableStateOf(
            LatLng(
                currentPackage.value.location.latitude,
                currentPackage.value.location.longitude
            )
        )
    }

    val startLocation = CurrentSession.route.value.startLocation

    currentLocation = remember {
        mutableStateOf(
            LatLng(
                startLocation.latitude,
                startLocation.longitude
            )
        )
    }

    pointsList = atlasViewModel.observePointsList().observeAsState(emptyList())

    cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition.value, 20f)
    }

    Scaffold(
        topBar = {
            AppBar(
                navigationIcon = Icons.Filled.ArrowBack,
                onNavigationIconClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            GoogleMap(
                modifier = Modifier.fillMaxHeight(.35f),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapStyleOptions = MapStyleOptions(atlasViewModel.getMapStyleString())
                )
            ) {

                Marker(
                    state = MarkerState(position = currentLocation.value),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.maps_dot)
                )

                markerPosition.value = LatLng(
                    currentPackage.value.location.latitude,
                    currentPackage.value.location.longitude
                )
                Marker(
                    state = MarkerState(position = markerPosition.value),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_black_marker)
                )

                Marker(
                    state = MarkerState(
                        position = LatLng(
                            endLocation.latitude,
                            endLocation.longitude
                        )
                    ),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.finish)
                )

                Polyline(
                    points = pointsList.value,
                    startCap = RoundCap(),
                    endCap = SquareCap()
                )
            }



            if (packagesList.value.isNotEmpty()) {

                Text(
                    text = "Next Package:",
                    style = PackForYouTypography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 15.dp, bottom = 10.dp, top = 12.dp)
                )
                Divider(Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "REF ${currentPackage.value.numPackage}",
                        style = PackForYouTypography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                                Box(modifier = Modifier.padding(top = 5.dp)) {
                                    PackageCard(pckge = currentPackage.value, isStartRoute = true)
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                StateIcon(
                                    state = currentPackage.value.state,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                Column {
                    Row(Modifier.padding(horizontal = 10.dp)) {
                        //BACK ARROW
                        if (currentPosition.value != 0) {
                            Surface(onClick = {
                                onBackArrowClick()
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    previousPackage.value =
                                        packagesList.value[currentPosition.value - 1]
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Go to previous package"
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "REF ${previousPackage.value.numPackage}",
                                        style = PackForYouTypography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        //FORWARD ARROW
                        if (currentPosition.value != packagesList.value.lastIndex) {
                            Surface(onClick = {
                                onForwardArrowClick()
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    nextPackage.value =
                                        packagesList.value[currentPosition.value + 1]
                                    Text(
                                        text = "REF ${nextPackage.value.numPackage}",
                                        style = PackForYouTypography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Go to next package"
                                    )
                                }
                            }
                        }
                    }

                    MarkAsDeliveredButton(packagesViewModel, currentLocation, navController)
                }
            }
        }
    }
}

private fun onBackArrowClick() {
    currentPackage.value = packagesList.value[currentPosition.value - 1]
    currentPosition.value--
    markerPosition.value = LatLng(
        currentPackage.value.location.latitude,
        currentPackage.value.location.longitude
    )
    cameraPositionState.position =
        CameraPosition.fromLatLngZoom(markerPosition.value, 17f)
}

private fun onForwardArrowClick() {
    currentPackage.value = packagesList.value[currentPosition.value + 1]
    currentPosition.value++
    markerPosition.value = LatLng(
        currentPackage.value.location.latitude,
        currentPackage.value.location.longitude
    )
    cameraPositionState.position =
        CameraPosition.fromLatLngZoom(markerPosition.value, 17f)
}

@Composable
fun MarkAsDeliveredButton(
    viewModel: IPackagesViewModel,
    currentLocation: MutableState<LatLng>,
    navController: NavController
) {
    val context = LocalContext.current
    Button(
        onClick = {
            val pckgPos = currentPosition.value
            val pckgListLastIndex = packagesList.value.lastIndex

            currentPackage.value.isDelivered = true
            viewModel.removePackageFromToDeliverList(currentPackage.value)


            if (pckgPos != pckgListLastIndex) {
                onMarkAsDeliveredClick(currentLocation)
            } else if (packagesList.value.size > 1) { //situated on last package but this is not the only one
                //we go to the beginning of the route
                currentPosition.value = 0
                onMarkAsDeliveredClick(currentLocation)
            } else { //last Package
                Toast.makeText(
                    context,
                    "You have delivered all the packages. Time to go home :D!",
                    Toast.LENGTH_LONG
                ).show()
                navController.popBackStack()
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Black),
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Text(
                text = "Delivered",
                color = White,
                style = PackForYouTypography.bodyMedium
            )
        }
    }
}

private fun onMarkAsDeliveredClick(currentLocation: MutableState<LatLng>) {
    //We set the previous location of the deliverMan
    val previousLocation = currentPackage.value.location
    currentLocation.value = LatLng(previousLocation.latitude, previousLocation.longitude)
    CurrentSession.deliveryMan!!.currentLocation = previousLocation

    //as the packagesList has changed, now on the currentPosition it is what previously was "next package"
    currentPackage.value = packagesList.value[currentPosition.value]
    markerPosition.value = LatLng(
        currentPackage.value.location.latitude,
        currentPackage.value.location.longitude
    )

    cameraPositionState.position =
        CameraPosition.fromLatLngZoom(markerPosition.value, 17f)
}