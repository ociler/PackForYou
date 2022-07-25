package com.packforyou.ui.packages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.packforyou.data.models.Package
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveredPackages(dialogState: MutableState<Boolean>, deliveredPackages: List<Package>) {
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
                                text = "Delivered",
                                fontSize = 18.sp,
                                style = PackForYouTypography.displayMedium
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(top = 25.dp)
                        .fillMaxHeight()
                ) {
                    items(deliveredPackages) { pckge ->
                        SimplePackageItem(pckge = pckge, modifier = Modifier.padding(horizontal = 30.dp))
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}