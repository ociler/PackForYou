package com.packforyou.ui.packages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.packforyou.R
import com.packforyou.data.models.Location
import com.packforyou.ui.atlas.IAtlasViewModel
import com.packforyou.ui.atlas.bitmapDescriptorFromVector
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White

lateinit var selectedLocation: MutableState<Location>

@Composable
fun SetLastLocation(
    dialogState: MutableState<Boolean>,
    lastLocations: List<Location>,
    viewModel: IPackagesViewModel
) {

    selectedLocation = if (lastLocations.isNotEmpty()) {
        remember {
            mutableStateOf(lastLocations[lastLocations.indexOf(CurrentSession.deliveryMan!!.lastLocation)])
        }
    } else {
        remember { mutableStateOf(Location()) }
    }

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
                        .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 15.dp),
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
                                text = "Set Last Location",
                                fontSize = 18.sp,
                                style = PackForYouTypography.displayMedium
                            )
                        }
                    }
                }

                Divider(Modifier.fillMaxWidth())

                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {

                    lastLocations.forEach { location ->
                        item {
                            SetLastLocationItem(location = location, viewModel = viewModel)
                            Divider(color = Black, modifier = Modifier.fillMaxWidth())
                        }
                    }

                    item {
                        SetNewEndLocationItem(viewModel = viewModel)
                        Divider(color = Black, modifier = Modifier.fillMaxWidth())
                    }

                }

                SetLocationButton(
                    location = selectedLocation.value,
                    viewModel = viewModel,
                    dialogState = dialogState
                )
            }
        }
    }
}

@Composable
fun SetLastLocationItem(location: Location, viewModel: IPackagesViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectedLocation.value = location
            }
            .padding(horizontal = 10.dp)
    ) {
        RadioButton(
            selected = selectedLocation.value.latitude == location.latitude
                    && selectedLocation.value.longitude == location.longitude,
            onClick = {
                selectedLocation.value = location
            }
        )

        Text(
            text = location.address,
            style = PackForYouTypography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 10.dp)
                .fillMaxWidth(.8f)
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {
                viewModel.removeLastLocation(location)
            },
            modifier = Modifier.padding(end = 10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Removes Last Location"
            )

        }
    }
}

@Composable
fun SetLocationButton(
    location: Location,
    viewModel: IPackagesViewModel,
    dialogState: MutableState<Boolean>
) {
    Button(
        onClick = {
            viewModel.setLastLocation(location)
            viewModel.computeProperAlgorithmAndUpdateCurrentSession(
                CurrentSession.algorithm,
                CurrentSession.packagesToDeliver.value
            )

            dialogState.value = false
        },
        colors = ButtonDefaults.buttonColors(containerColor = Black),
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Text(
            text = "Set End Location",
            color = White,
            style = PackForYouTypography.displayLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetNewEndLocationItem(viewModel: IPackagesViewModel) {

    var addressText by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    //We define this mutable location and it will get as address the user's input
    val newLocation by remember {
        mutableStateOf(Location(latitude = -1.0, address = addressText))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectedLocation.value = newLocation
            }
            .padding(horizontal = 10.dp)
    ) {
        TextField(
            value = addressText,
            onValueChange = {
                addressText = it
                newLocation.address = it
            },
            label = { Text(text = "New Location") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Black,
                focusedLabelColor = Color.Transparent
            ),
            trailingIcon = {
                IconButton(onClick = { addressText = "" }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Removes content"
                    )
                }
            },
            textStyle = PackForYouTypography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth(.8f)
                .padding(horizontal = 5.dp, vertical = 10.dp)
        )

        Spacer(Modifier.weight(1f))

        IconButton(
            onClick = { addNewLocation(newLocation, context = context, viewModel = viewModel) },
            modifier = Modifier.padding(end = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Confirms new last location"
            )

        }
    }
}

private fun addNewLocation(newLocation: Location, context: Context, viewModel: IPackagesViewModel) {
    val latLong = viewModel.getLocationFromAddress(newLocation.address, context)

    if (latLong != null) {
        val location = Location(
            address = newLocation.address,
            latitude = latLong.latitude,
            longitude = latLong.longitude
        )

        viewModel.addLastLocation(location)
    } else {
        Toast.makeText(
            context,
            "This address is not valid. Please write a valid address.",
            Toast.LENGTH_LONG
        ).show()
    }
}