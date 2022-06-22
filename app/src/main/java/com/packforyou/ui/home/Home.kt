package com.packforyou.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.packforyou.data.models.Route
import com.packforyou.ui.atlas.Atlas
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.atlas.CasetaAtlas
import com.packforyou.ui.theme.PackForYouTypography
import kotlinx.coroutines.launch

//This will be the main screen. Exists just to be able to use the packages and the map on the same screen

lateinit var addPackageState: MutableState<Boolean>
lateinit var editPackageState: MutableState<Boolean>
lateinit var setEndLocationState: MutableState<Boolean>

lateinit var actionTitle: String

@Composable
fun Home(owner: ViewModelStoreOwner, route: Route) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

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

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {


            DrawerHeader(scaffoldState)
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
                        scaffoldState.drawerState.close()
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.BottomCenter)
                    .padding(8.dp)
            ) {
                DrawerFooter(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Atlas(
                    atlasViewModel = atlasViewModel,
                    route = route
                )
            }
        }

    )

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
            dismissOnBackPress = false,
            dismissOnClickOutside = false
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
                .fillMaxHeight(.6f)
                .fillMaxWidth(1f)
                .padding(horizontal = 20.dp),
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
            .fillMaxHeight(.95f)
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
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(start = 58.dp)
            )

        }
    }
}

@Composable
private fun BottomButtons(successButtonText: String, dialogState: MutableState<Boolean>) {
    var directionText by remember { mutableStateOf("") }
    var clientText by remember { mutableStateOf("") }
    var sendType by remember { mutableStateOf("Hello World") }

    Column(
        modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxWidth(1f)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = directionText,
            onValueChange = { directionText = it },
            label = { Text("Direction") }
        )

        OutlinedTextField(
            value = clientText,
            onValueChange = { clientText = it },
            label = { Text("Client") }
        )



        Button(
            onClick = {
                dialogState.value = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = successButtonText, fontSize = 20.sp)
        }

    }
}