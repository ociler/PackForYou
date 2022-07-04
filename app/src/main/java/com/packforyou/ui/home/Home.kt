package com.packforyou.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.packforyou.R
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import com.packforyou.ui.atlas.Atlas
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.packages.AddPackage
import com.packforyou.ui.packages.SelectPackageToEdit
import com.packforyou.ui.packages.Packages
import com.packforyou.ui.packages.PackagesViewModelImpl
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White
import kotlinx.coroutines.launch

//This will be the main screen. Exists just to be able to use the packages and the map on the same screen

lateinit var addPackageState: MutableState<Boolean>
lateinit var selectPackageToEditState: MutableState<Boolean>
lateinit var setEndLocationState: MutableState<Boolean>

var choosenPackage = Package() //TODO


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Home(owner: ViewModelStoreOwner, route: Route) {
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    val packagesViewModel =
        ViewModelProvider(owner)[PackagesViewModelImpl::class.java]

    val atlasViewModel =
        ViewModelProvider(owner)[AtlasViewModelImpl::class.java]


    addPackageState = remember {
        mutableStateOf(false)
    }

    selectPackageToEditState = remember {
        mutableStateOf(false)
    }

    setEndLocationState = remember {
        mutableStateOf(false)
    }

    BottomSheetScaffold(
        scaffoldState = sheetState,
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        sheetState.drawerState.open()
                    }
                },
                packagesViewModel
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

                        "setLatestLocation" -> {
                            setEndLocationState.value = true
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
            Packages(packagesViewModel = packagesViewModel)
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

                    StartRouteRoundedButton(
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

    if (setEndLocationState.value) {
        SetEndLocation()
    }
}

@Composable
fun SetEndLocation() {
    // Context to toast a message
    val ctx: Context = LocalContext.current

    // Code to Show and Dismiss Dialog
    if (addPackageState.value) {
        Dialog(
            onDismissRequest = { setEndLocationState.value = false },
            content = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    "Closes alert",
                    modifier = Modifier.clickable {
                        addPackageState.value = false
                    }
                )

                CompleteDialogContent("Set End Location", addPackageState, "OK")
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    } else {
        Toast.makeText(ctx, "Dialog Closed", Toast.LENGTH_SHORT).show()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteDialogContent(
    title: String,
    dialogState: MutableState<Boolean>,
    successButtonText: String
) {
    Card(
        modifier = Modifier
            .fillMaxHeight(.96f)
            .fillMaxWidth(1f),
        shape = RoundedCornerShape(40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

        }
    }
}


@ExperimentalMaterialApi
@Composable
fun HomeScreen() {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()

}

@Composable
fun StartRouteRoundedButton(modifier: Modifier = Modifier) {
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
                    println("Starting route")
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