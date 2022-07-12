package com.packforyou.ui.packages

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.packforyou.R
import com.packforyou.data.models.Package
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.atlas.IAtlasViewModel
import com.packforyou.ui.home.AppBar
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartRouteScreen(
    packagesList: List<Package>,
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
        mutableStateOf(packagesList[0])
    }

    var previousPackage by remember {
        mutableStateOf(packagesList[0])
    }

    var nextPackage by remember {
        mutableStateOf(packagesList[1])
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
    ) {
        Column(modifier = Modifier.padding(it)) {

            GoogleMap(
                modifier = Modifier.fillMaxHeight(.35f),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapStyleOptions = MapStyleOptions(atlasViewModel.getMapStyleString())
                )
            ) {
                packagesList.forEach {
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
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "REF ${currentPackage.numPackage}",
                    style = PackForYouTypography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn {
                    item {
                        PackageItem(pckge = currentPackage)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            //BACK ARROW
            Row(Modifier.padding(horizontal = 10.dp)) {
                if (currentPosition != 0) {
                    Surface(onClick = {
                        currentPackage = packagesList[currentPosition - 1]
                        currentPosition--
                        markerPosition = LatLng(
                            currentPackage.location.latitude,
                            currentPackage.location.longitude
                        )
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(markerPosition, 18f)
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            previousPackage = packagesList[currentPosition - 1]
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
                if (currentPosition != packagesList.lastIndex) {
                    Surface(onClick = {
                        currentPackage = packagesList[currentPosition + 1]
                        currentPosition++
                        markerPosition = LatLng(
                            currentPackage.location.latitude,
                            currentPackage.location.longitude
                        )
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(markerPosition, 18f)
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            nextPackage = packagesList[currentPosition + 1]
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

            MarkAsDeliveredButton(currentPackage)
        }
    }
}

@Composable
fun MarkAsDeliveredButton(pckg: Package) {
    Button(
        onClick = {
            pckg.isDelivered = true
            //TODO we need to do something else I guess
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