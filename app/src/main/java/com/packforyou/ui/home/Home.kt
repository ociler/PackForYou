package com.packforyou.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.packforyou.R
import com.packforyou.data.models.Algorithm
import com.packforyou.navigation.Screen
import com.packforyou.ui.atlas.AtlasScreen
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.packages.*
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.TravelTimeButtonColor
import com.packforyou.ui.theme.White
import kotlinx.coroutines.launch
import java.time.LocalDate

//This will be the main screen. Exists just to be able to use the packages and the map on the same screen

private lateinit var addPackageState: MutableState<Boolean>
private lateinit var selectPackageToEditState: MutableState<Boolean>
private lateinit var defineEndLocationState: MutableState<Boolean>

private var isFirstScreen = false


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModelOwner: ViewModelStoreOwner,
    lifecycleOwner: LifecycleOwner
) {

    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val packagesViewModel =
        ViewModelProvider(viewModelOwner)[PackagesViewModelImpl::class.java]

    val atlasViewModel =
        ViewModelProvider(viewModelOwner)[AtlasViewModelImpl::class.java]

    val loginViewModel =
        ViewModelProvider(viewModelOwner)[LoginViewModelImpl::class.java]


    if (isFirstScreen) { //to not to login everytime
        isFirstScreen = false
        val deliveryMan = loginViewModel.getExampleDeliveryMan()

        deliveryMan.route!!.packages.forEachIndexed { index, pckg ->
            pckg.position = index
            pckg.deliveryDate = LocalDate.now()
        }

        CurrentSession.route = remember {
            mutableStateOf(deliveryMan.route!!)
        }

        CurrentSession.packagesForToday = if (deliveryMan.route != null) {
            remember {
                mutableStateOf(deliveryMan.route!!.packages)
            }
        } else {
            remember {
                mutableStateOf(listOf())
            }
        }

        CurrentSession.packagesToDeliver = remember {
            mutableStateOf(CurrentSession.packagesForToday.value)
        }

        CurrentSession.lastLocationsList =
            remember {
                mutableStateOf(deliveryMan.lastLocationList)
            }

        CurrentSession.deliveryMan = deliveryMan

        CurrentSession.travelTime = remember {
            mutableStateOf(deliveryMan.route!!.totalTime!!)
        }

        IsLoading.state = remember { mutableStateOf(false) }
    }

    val packages = CurrentSession.packagesToDeliver

    addPackageState = remember {
        mutableStateOf(false)
    }

    selectPackageToEditState = remember {
        mutableStateOf(false)
    }

    defineEndLocationState = remember {
        mutableStateOf(false)
    }


    BottomSheetScaffold(
        scaffoldState = sheetState,
        topBar = {
            AppBar(
                navigationIcon = Icons.Filled.Menu,
                onNavigationIconClick = {
                    scope.launch {
                        sheetState.drawerState.open()
                    }
                }
            )
        },
        drawerGesturesEnabled = sheetState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader(sheetState)
            DrawerBody(
                items = listOf(
                    MenuItem(
                        id = "edit",
                        title = "Edit Package",
                        contentDescription = "Edit Package"
                    ),

                    MenuItem(
                        id = "add",
                        title = "Add Package",
                        contentDescription = "Add package"
                    ),

                    MenuItem(
                        id = "setLastLocation",
                        title = "Set last location",
                        contentDescription = "Set last location"
                    )
                ),
                onItemClick = {
                    when (it.id) {
                        "edit" -> {
                            selectPackageToEditState.value = true
                        }

                        "add" -> {
                            addPackageState.value = true
                        }

                        "setLastLocation" -> {
                            defineEndLocationState.value = true
                        }
                    }

                    scope.launch {
                        sheetState.drawerState.close()
                    }
                }
            )

            Spacer(Modifier.weight(1f))
            DrawerFooter(navController = navController, loginViewModel = loginViewModel)
        },
        sheetContent = {
            PackagesScreen(
                navController = navController,
                packagesViewModel = packagesViewModel,
                packages = packages,
                lifecycleOwner = lifecycleOwner
            )
        },
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetElevation = 10.dp,
        sheetPeekHeight = 100.dp,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .fillMaxHeight()

    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Box(Modifier.fillMaxSize()) {
                AtlasScreen(
                    atlasViewModel = atlasViewModel
                )

                Column {
                    Spacer(modifier = Modifier.weight(1f))

                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        TravelTimeButton(
                            modifier = Modifier.padding(
                                end = 15.dp,
                                bottom = 10.dp
                            )
                        )
                    }

                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        StartRouteRoundedButton(
                            navController = navController,
                            modifier = Modifier.padding(
                                end = 15.dp,
                                bottom = 10.dp
                            ),
                            packages.value.isEmpty()
                        )
                    }
                }
            }
        }
    }


    if (addPackageState.value) {
        if (CurrentSession.packagesToDeliver.value.size >= 10 && CurrentSession.algorithm == Algorithm.BRUTE_FORCE) {
            Toast.makeText(
                context,
                "Sorry, the maximum number of packages using the Brute Force Algorithm is 10." +
                        " Please, change the sorting algorithm before adding one more package.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            AddPackage(dialogState = addPackageState, owner = viewModelOwner)
        }
    }

    if (selectPackageToEditState.value) {
        SelectPackageToEdit(
            dialogState = selectPackageToEditState,
            packages = packages,
            owner = viewModelOwner
        )
    }

    if (defineEndLocationState.value) {
        SetLastLocation(
            dialogState = defineEndLocationState,
            lastLocations = CurrentSession.lastLocationsList.value,
            viewModel = packagesViewModel
        )
    }
}

@Composable
fun StartRouteRoundedButton(
    navController: NavController,
    modifier: Modifier = Modifier,
    isEmpty: Boolean
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Black,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    if (isEmpty) {
                        Toast
                            .makeText(
                                context,
                                "You have no packages to deliver, so you can't Start a route.",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    } else {
                        navController.navigate(route = Screen.StartRoute.route)
                    }
                }
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Start Route",
                    color = White,
                    style = PackForYouTypography.bodyMedium,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_navigation),
                contentDescription = "Starts the navigation",
                tint = White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun TravelTimeButton(modifier: Modifier = Modifier) {

    val travelTime = CurrentSession.travelTime
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TravelTimeButtonColor,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 15.dp)
        ) {
            Text(
                text = "${travelTime.value / 60} min",
                color = Black,
                style = PackForYouTypography.bodyMedium,
                fontSize = 16.sp
            )

        }
    }
}
