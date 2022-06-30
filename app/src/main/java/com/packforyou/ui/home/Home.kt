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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.packforyou.data.models.Route
import com.packforyou.ui.atlas.Atlas
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.atlas.CasetaAtlas
import com.packforyou.ui.packages.Packages
import com.packforyou.ui.packages.PackagesViewModelImpl
import com.packforyou.ui.packages.StartRouteRectangularButton
import com.packforyou.ui.theme.*
import kotlinx.coroutines.launch

//This will be the main screen. Exists just to be able to use the packages and the map on the same screen

lateinit var addPackageState: MutableState<Boolean>
lateinit var editPackageState: MutableState<Boolean>
lateinit var setEndLocationState: MutableState<Boolean>


lateinit var actionTitle: String

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

    editPackageState = remember {
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
                            editPackageState.value = true
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
        AddPackage()
    }

    if (editPackageState.value) {
        EditPackage()
    }

    if (setEndLocationState.value) {
        SetEndLocation()
    }
}


@Composable
fun AddPackage() {
    Dialog(
        onDismissRequest = { addPackageState.value = false },
        content = {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                "Closes alert",
                modifier = Modifier.clickable {
                    addPackageState.value = false
                }
            )
            CompleteDialogContent("Add package", addPackageState, "Add package")

        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

@Composable

fun EditPackage() {
    // Context to toast a message
    val ctx: Context = LocalContext.current

    // Code to Show and Dismiss Dialog
    if (addPackageState.value) {
        Dialog(
            onDismissRequest = { addPackageState.value = false },
            content = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    "Closes alert",
                    modifier = Modifier.clickable {
                        addPackageState.value = false
                    }
                )

                CompleteDialogContent("Edit Package", addPackageState, "OK")
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

@Composable
fun SetEndLocation() {
    // Context to toast a message
    val ctx: Context = LocalContext.current

    // Code to Show and Dismiss Dialog
    if (addPackageState.value) {
        Dialog(
            onDismissRequest = { addPackageState.value = false },
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
fun BodyContent() {
    Card(
        modifier = Modifier
            .fillMaxHeight(.48f)
            .fillMaxWidth(1f)
            .padding(top = 10.dp, start = 20.dp, end = 20.dp),
        shape = RoundedCornerShape(40.dp)
    ) {
        CasetaAtlas()
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
            TitleAndButton(title, dialogState)
            BodyContent()
            BottomButtons(successButtonText, dialogState)
        }
    }
}

@Composable
private fun TitleAndButton(title: String, dialogState: MutableState<Boolean>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(modifier = Modifier.then(Modifier.size(24.dp)),
                onClick = {
                    dialogState.value = false
                }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    "Closes the dialog"
                )
            }

            Text(
                text = title,
                fontSize = 18.sp,
                style = PackForYouTypography.displayMedium,
                modifier = Modifier.padding(start = 58.dp)
            )

        }
    }
}

@Composable
private fun BottomButtons(successButtonText: String, dialogState: MutableState<Boolean>) {
    var directionText by remember { mutableStateOf("") }
    var clientText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {

        OutlinedTextField(
            value = directionText,
            onValueChange = { directionText = it },
            label = { Text("Direction") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Black,
                unfocusedBorderColor = Black,
                textColor = Black
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Removes content",
                    modifier = Modifier.clickable {
                        directionText = ""
                    }
                )
            }
        )

        Spacer(Modifier.height(5.dp))

        OutlinedTextField(
            value = clientText,
            onValueChange = { clientText = it },
            label = { Text("Client") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Black,
                unfocusedBorderColor = Black,
                textColor = Black
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Removes content",
                    modifier = Modifier.clickable {
                        clientText = ""
                    }
                )
            }
        )

        Spacer(Modifier.height(5.dp))

        UrgencySpinner() //TODO change style

        Button(
            onClick = {
                dialogState.value = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = successButtonText,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 5.dp),
                color = White
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrgencySpinner() {

    val urgencyOptions = listOf(
        "Very Urgent",
        "Urgent",
        "Not Urgent"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(urgencyOptions[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = Modifier.padding(top = 5.dp)
    ) {
        TextField(
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text("Urgency") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = CustomExposedDropdownMenu()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            urgencyOptions.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    },
                    text = {
                        Text(text = selectionOption)
                    }
                )
            }
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