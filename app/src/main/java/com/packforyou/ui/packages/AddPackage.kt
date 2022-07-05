package com.packforyou.ui.packages

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.packforyou.data.models.Package
import com.packforyou.ui.atlas.CasetaAtlas
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.CustomExposedDropdownMenu
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPackage(dialogState: MutableState<Boolean>, packge: Package? = null) {

    var directionText by remember { mutableStateOf("") }
    var clientText by remember { mutableStateOf("") }


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
                                text = "Add Package",
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

                    PackageAtlas()

                    Column( //TextFields and bottom button
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
                                dialogState.value = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Add Package",
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
fun PackageAtlas() {
    Card( //Map
        modifier = Modifier
            .fillMaxHeight(.48f)
            .fillMaxWidth(1f)
            .padding(top = 10.dp, start = 20.dp, end = 20.dp),
        shape = RoundedCornerShape(40.dp)
    ) {
        CasetaAtlas() //TODO add corresponding map
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


