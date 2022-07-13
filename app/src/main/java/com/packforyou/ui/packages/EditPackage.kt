package com.packforyou.ui.packages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelStoreOwner
import com.packforyou.data.models.Package
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White

private lateinit var editPackageState: MutableState<Boolean>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPackageToEdit(
    dialogState: MutableState<Boolean>,
    packages: List<Package>,
    owner: ViewModelStoreOwner
) {
    var selectedPackage by remember { mutableStateOf(packages[0]) }
    editPackageState = remember { mutableStateOf(false) }

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
                        .padding(20.dp),
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
                                text = "Edit package",
                                fontSize = 18.sp,
                                style = PackForYouTypography.displayMedium
                            )
                        }
                    }
                }

                Divider(color = Black, modifier = Modifier.fillMaxWidth())
                Text(
                    text = "Choose one package:",
                    style = PackForYouTypography.bodyMedium,
                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp)
                )
                Divider(color = Black, modifier = Modifier.fillMaxWidth())

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    items(packages) { pckge ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPackage = pckge
                                }
                                .padding(horizontal = 5.dp)
                        ) {

                            RadioButton(
                                selected = selectedPackage.numPackage == pckge.numPackage,
                                onClick = { selectedPackage = pckge }
                            )
                            SimplePackageItem(
                                pckge = pckge,
                                modifier = Modifier
                                    .padding(end = 25.dp)
                            )
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
                EditPackageButton(
                    selectedPackage = selectedPackage
                )
            }
        }
    }

    if (editPackageState.value) {
        //I give dialogState bc this way, when the user closes the AddPackage Dialog
        //he will also close the SelectPackageToEdit one
        AddPackage(dialogState = dialogState, packge = selectedPackage, owner = owner)
    }
}


@Composable
fun EditPackageButton(selectedPackage: Package) {
    Button(
        onClick = {
            editPackageState.value = true
        },
        colors = ButtonDefaults.buttonColors(containerColor = Black),
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 18.sp)) {
                    append("Edit: ")
                }
                withStyle(style = SpanStyle(fontSize = 15.sp)) {
                    append("Package ${selectedPackage.numPackage}")
                }
            },
            color = White,
            style = PackForYouTypography.bodyMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

    }
}