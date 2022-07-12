package com.packforyou.ui.home

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.packforyou.R
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import com.packforyou.navigation.ArgumentsHolder
import com.packforyou.navigation.Screen
import com.packforyou.ui.atlas.Atlas
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.packages.*
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White
import kotlinx.coroutines.launch

//This will be the main screen. Exists just to be able to use the packages and the map on the same screen

lateinit var addPackageState: MutableState<Boolean>
lateinit var selectPackageToEditState: MutableState<Boolean>
lateinit var defineEndLocationState: MutableState<Boolean>


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController, owner: ViewModelStoreOwner, route: Route) {

    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    val packagesViewModel =
        ViewModelProvider(owner)[PackagesViewModelImpl::class.java]

    val atlasViewModel =
        ViewModelProvider(owner)[AtlasViewModelImpl::class.java]

    ArgumentsHolder.packagesList = packagesViewModel.getExamplePackages()
    ArgumentsHolder.atlasViewModel = atlasViewModel

    val packages = remember {
        mutableStateOf(ArgumentsHolder.packagesList)
    }

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
                },
                packagesViewModel = packagesViewModel
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
            DrawerFooter()
        },
        sheetContent = {
            Packages(navController = navController, packagesViewModel = packagesViewModel, packages = packages)
        },
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetElevation = 10.dp,
        sheetPeekHeight = 70.dp,
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
                Atlas(
                    atlasViewModel = atlasViewModel,
                    route = route
                )
                Column {
                    Spacer(modifier = Modifier.weight(1f))

                    StartRouteRoundedButton(navController = navController,
                        packagesToStartRoute = packages.value,
                        modifier = Modifier.padding(
                            start = 15.dp,
                            bottom = 10.dp
                        )
                    )
                }
            }
        }
    }


    if (addPackageState.value) {
        AddPackage(addPackageState)
    }

    if (selectPackageToEditState.value) {
        SelectPackageToEdit(selectPackageToEditState, packagesViewModel.getExamplePackages())
    }

    if (defineEndLocationState.value) {
        SetLastLocation(
            dialogState = defineEndLocationState,
            lastLocations = listOf(
                Location(address = "Valencia"),
                Location(address = "El Hierro la mejor isla del mundo entero ", latitude = 2.0),
                Location(address = "El Hierro la mejor isla del mundo entero ", latitude = 3.0),
                Location(address = "El Hierro la mejor isla del mundo entero ", latitude = 4.0),
            )
        )
    }
}

@Composable
fun StartRouteRoundedButton(navController: NavController, packagesToStartRoute: List<Package>, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Black,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    //TODO start route
                    ArgumentsHolder.packagesList = packagesToStartRoute
                    navController.navigate(route = Screen.StartRoute.route)

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
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_navigation),
                contentDescription = "Starts the navigation",
                tint = White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}