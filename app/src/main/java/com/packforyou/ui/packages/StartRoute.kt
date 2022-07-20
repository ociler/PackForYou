package com.packforyou.ui.packages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.packforyou.R
import com.packforyou.data.models.Package
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.home.AppBar
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartRouteScreen(
    packagesList: MutableState<List<Package>>,
    navController: NavController,
    owner: ViewModelStoreOwner
) {

    val packagesViewModel =
        ViewModelProvider(owner)[PackagesViewModelImpl::class.java]

    val atlasViewModel =
        ViewModelProvider(owner)[AtlasViewModelImpl::class.java]

    var currentPosition by remember {
        mutableStateOf(0)
    }

    var currentPackage by remember {
        mutableStateOf(packagesList.value[0])
    }

    var previousPackage by remember {
        mutableStateOf(packagesList.value[0])
    }

    var nextPackage by remember {
        mutableStateOf(packagesList.value[1])
    }

    var markerPosition = LatLng(currentPackage.location.latitude, currentPackage.location.longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 20f)
    }

    Scaffold(
        topBar = {
            AppBar(
                navigationIcon = Icons.Filled.ArrowBack,
                onNavigationIconClick = {
                    navController.popBackStack()
                },
                packagesViewModel = packagesViewModel
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
                packagesList.value.forEach {
                    markerPosition = LatLng(it.location.latitude, it.location.longitude)
                    Marker(
                        state = MarkerState(position = markerPosition),
                        title = it.numPackage.toString(),
                        snippet = it.location.address,
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_black_marker)
                    )
                }

                Polyline(
                    points = atlasViewModel.observePointsList().value!!, //we will already have some route to show
                    startCap = RoundCap(),
                    endCap = SquareCap()
                )
            }

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
                    text = "REF ${currentPackage.numPackage}",
                    style = PackForYouTypography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                            Box(modifier = Modifier.padding(top = 5.dp)) {
                                PackageCard(pckge = currentPackage, isStartRoute = true)
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            StateIcon(
                                state = currentPackage.state,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
            //TODO pensar la millor manera de solucionar quan hi ha molt de text en la targeta

            Column {
                Row(Modifier.padding(horizontal = 10.dp)) {
                    //BACK ARROW
                    if (currentPosition != 0) {
                        Surface(onClick = {
                            currentPackage = packagesList.value[currentPosition - 1]
                            currentPosition--
                            markerPosition = LatLng(
                                currentPackage.location.latitude,
                                currentPackage.location.longitude
                            )
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(markerPosition, 18f)
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                previousPackage = packagesList.value[currentPosition - 1]
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Go to previous package"
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "REF ${previousPackage.numPackage}",
                                    style = PackForYouTypography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    //FORWARD ARROW
                    if (currentPosition != packagesList.value.lastIndex) {
                        Surface(onClick = {
                            currentPackage = packagesList.value[currentPosition + 1]
                            currentPosition++
                            markerPosition = LatLng(
                                currentPackage.location.latitude,
                                currentPackage.location.longitude
                            )
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(markerPosition, 18f)
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                nextPackage = packagesList.value[currentPosition + 1]
                                Text(
                                    text = "REF ${nextPackage.numPackage}",
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
                MarkAsDeliveredButton(currentPackage, packagesViewModel)
            }
        }
    }
}

@Composable
fun MarkAsDeliveredButton(pckg: Package, viewModel: IPackagesViewModel) {
    Button(
        onClick = {
            pckg.isDelivered = true
            viewModel.removePackageFromToDeliverList(pckg)
            //TODO fix the mark as delivered and outbounds issue
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