package com.packforyou.ui.packages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.packforyou.R
import com.packforyou.data.models.*
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.CustomExposedDropdownMenu
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White
import java.util.*
import kotlin.random.Random

private var selectedOption = Urgency.NOT_URGENT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPackage(
    dialogState: MutableState<Boolean>,
    packge: Package? = null,
    owner: ViewModelStoreOwner
) {
    val packageAddress = packge?.location?.address ?: ""
    val packageClient = packge?.client?.name ?: ""

    var addressText by remember { mutableStateOf(packageAddress) }
    var clientText by remember { mutableStateOf(packageClient) }

    val atlasViewModel =
        ViewModelProvider(owner)[AtlasViewModelImpl::class.java]
    val packagesViewModel =
        ViewModelProvider(owner)[PackagesViewModelImpl::class.java]

    var parsedLatLng: LatLng? by remember { mutableStateOf(LatLng(39.485749, -0.3563635)) }
    val focusRequester = FocusRequester()
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(parsedLatLng!!, 20f)
    }


    val caption = if (packge == null) "Add Package" else "Edit Package"


    Dialog(
        onDismissRequest = { dialogState.value = false },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxHeight(.9f)
                .fillMaxWidth(1f),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(White)
        ) {

            Column {

                Row( //Top bar
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box {
                        IconButton(modifier = Modifier.then(Modifier.size(24.dp)),
                            onClick = {
                                dialogState.value = false
                            }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                "Closes the dialog"
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = caption,
                                fontSize = 18.sp,
                                style = PackForYouTypography.displayMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }


                Column(//Body
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    //MAP
                    Card(
                        modifier = Modifier
                            .fillMaxHeight(.4f)
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 20.dp, end = 20.dp),
                        shape = RoundedCornerShape(40.dp)
                    ) {

                        GoogleMap(
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(
                                mapStyleOptions = MapStyleOptions(atlasViewModel.getMapStyleString())
                            )
                        ) {
                            if (parsedLatLng != null) {
                                Marker(
                                    state = MarkerState(position = parsedLatLng!!),
                                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_black_marker)
                                )
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(parsedLatLng!!, 17f)
                            }
                        }
                    }


                    Column( //TextFields and bottom button
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {

                        OutlinedTextField(
                            value = addressText,
                            onValueChange = { addressText = it },
                            label = { Text("Address") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Black,
                                unfocusedBorderColor = Black,
                                textColor = Black
                            ),
                            singleLine = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Removes content",
                                    modifier = Modifier.clickable {
                                        addressText = ""
                                    }
                                )
                            },
                            //I want to parse the input address when it's not focused anymore.
                            //This way it is not constantly trying to parse an uncompleted direction.
                            //It only will try the parse when the user finishes his input.
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    if (!it.isFocused) {
                                        parsedLatLng = packagesViewModel.getLocationFromAddress(
                                            context = context,
                                            address = addressText
                                        )
                                    }
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
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { clientText = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Removes content"
                                    )
                                }
                            }
                        )

                        Spacer(Modifier.height(5.dp))

                        UrgencySpinner() //TODO change style

                        Button(
                            onClick = {
                                if (parsedLatLng != null) {
                                    if (packge == null) {
                                        val newPackage = Package(
                                            numPackage = Random.nextInt(
                                                10,
                                                1000
                                            ), //TODO create hash function
                                            position = CurrentSession.packagesToDeliver.value.size,
                                            deliveryDate = Date(System.currentTimeMillis()),
                                            isDelivered = false,
                                            urgency = selectedOption,
                                            client = Client(name = clientText),
                                            location = Location(
                                                address = addressText,
                                                latitude = parsedLatLng!!.latitude,
                                                longitude = parsedLatLng!!.longitude
                                            ),
                                            state = PackageState.NOT_CONFIRMED
                                        )

                                        packagesViewModel.addPackage(newPackage)

                                    } else { //Editing package

                                        val newLocation = Location(
                                            address = addressText,
                                            latitude = parsedLatLng!!.latitude,
                                            longitude = parsedLatLng!!.longitude
                                        )

                                        val editedPackage = packge.copy(
                                            location = newLocation,
                                            client = packge.client.copy(name = clientText),
                                            urgency = selectedOption
                                        )

                                        packagesViewModel.removePackageFromToDeliverList(pckge = packge)
                                        packagesViewModel.addPackage(packge = editedPackage)
                                    }
                                    dialogState.value = false
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Address not recognized. Please set a valid address.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = caption,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 5.dp),
                                color = White
                            )
                        }

                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrgencySpinner() {

    val urgencyOptions = listOf(
        "Not Urgent",
        "Urgent",
        "Very Urgent"
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
            urgencyOptions.forEach { urgencyType ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = urgencyType
                        expanded = false
                        selectedOption = getUrgencyGivenText(selectedOptionText)
                    },
                    text = {
                        Text(text = urgencyType)
                    }
                )
            }
        }
    }
}


private fun getUrgencyGivenText(text: String): Urgency {
    return when (text) {
        "Very Urgent" -> Urgency.VERY_URGENT
        "Urgent" -> Urgency.URGENT
        else -> Urgency.NOT_URGENT
    }
}


