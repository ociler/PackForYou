package com.packforyou.ui.packages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.packforyou.ui.atlas.bitmapDescriptorFromVector
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White

lateinit var selectedLocation: MutableState<Location>
var isNewLocation = false


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetLastLocation(dialogState: MutableState<Boolean>, lastLocations: List<Location>) {
    selectedLocation = remember { mutableStateOf(lastLocations[0]) }

    Dialog(
        onDismissRequest = { dialogState.value = false },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxHeight(.96f)
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
                            SetLastLocationItem(location = location)
                            Divider(color = Black, modifier = Modifier.fillMaxWidth())
                        }
                    }

                    item {
                        SetNewEndLocationItem()
                        Divider(color = Black, modifier = Modifier.fillMaxWidth())
                    }

                }
                SetLocationButton()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetLastLocationItem(location: Location) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectedLocation.value = location
                isNewLocation = false
            }
            .padding(horizontal = 10.dp)
    ) {
        RadioButton(
            selected = selectedLocation.value.latitude == location.latitude
                    && selectedLocation.value.longitude == location.longitude,
            onClick = {
                selectedLocation.value = location
                isNewLocation = false
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
            onClick = { /*TODO remove this last location*/ },
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
fun SetLocationButton() {
    Button(
        onClick = {
            //TODO set this endLocation
            if (isNewLocation) {
                //parse
            }
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
fun SetNewEndLocationItem() {

    var addressText by remember {
        mutableStateOf("")
    }

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
                isNewLocation = true
            }
            .padding(horizontal = 10.dp)
    ) {
        RadioButton(
            selected = selectedLocation.value.address == newLocation.address,
            onClick = {
                selectedLocation.value = newLocation
                isNewLocation = true
            }
        )

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
            onClick = { /*TODO remove this last location*/ },
            modifier = Modifier.padding(end = 10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Removes Last Location"
            )

        }
    }
}