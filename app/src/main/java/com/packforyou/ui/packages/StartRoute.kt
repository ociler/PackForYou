package com.packforyou.ui.packages

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.packforyou.R
import com.packforyou.data.models.Package
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartRouteScreen(packagesList: List<Package>) {
    var currentPosition by remember {
        mutableStateOf(0)
    }

    var currentPackage by remember {
        mutableStateOf(packagesList[0])
    }

    var previousPackage by remember {
        mutableStateOf(packagesList[0])
    }

    var nextPackage by remember {
        mutableStateOf(packagesList[1])
    }

    Column {
        Text(
            text = "Next Package:",
            style = PackForYouTypography.bodyMedium,
            modifier = Modifier.padding(start = 15.dp, bottom = 10.dp)
        )
        Divider(Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "REF ${currentPackage.numPackage}",
                style = PackForYouTypography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            PackageItem(pckge = currentPackage)

            Row(Modifier.padding(horizontal = 10.dp)) {
                if (currentPosition != 0) {
                    Surface(onClick = {
                        currentPackage = packagesList[currentPosition - 1]
                        currentPosition--
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            previousPackage = packagesList[currentPosition - 1]
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go to previous package"
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "REF ${previousPackage.numPackage}",
                                style = PackForYouTypography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                if (currentPosition != packagesList.lastIndex) {
                    Surface(onClick = {
                        currentPackage = packagesList[currentPosition + 1]
                        currentPosition++
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            nextPackage = packagesList[currentPosition + 1]
                            Text(
                                text = "REF ${nextPackage.numPackage}",
                                style = PackForYouTypography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Go to next package"
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        MarkAsDeliveredButton(currentPackage)

    }
}

@Composable
fun MarkAsDeliveredButton(pckg: Package) {
    Button(
        onClick = {
            pckg.isDelivered = true
            //TODO we need to do something else I guess
        },
        colors = ButtonDefaults.buttonColors(containerColor = Black),
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Text(
                text = "Delivered",
                color = White,
                style = PackForYouTypography.bodyMedium
            )
        }
    }
}