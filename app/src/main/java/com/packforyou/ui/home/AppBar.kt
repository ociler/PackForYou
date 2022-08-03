package com.packforyou.ui.home

import android.graphics.fonts.FontStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.packforyou.R
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.packages.DeliveredPackages
import com.packforyou.ui.packages.IPackagesViewModel
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.Inter
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White


@Composable
fun AppBar(
    navigationIcon: ImageVector,
    onNavigationIconClick: () -> Unit
) {

    val deliveredPackagesState = remember {
        mutableStateOf(false)
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text =
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    ) {
                        append("Pack")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 30.sp
                        )
                    ) {
                        append("4")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    ) {
                        append("You")
                    }
                },
                textAlign = TextAlign.Center
            )
        },
        actions = {
            IconButton(onClick = {
                deliveredPackagesState.value = true
            }) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_delivered_packages),
                    contentDescription = "Delivered icon",
                    tint = Black
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = "Toggle drawer"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = White)
    )

    if (deliveredPackagesState.value) {
        val deliveredPackages = CurrentSession.packagesForToday.value.filter { pckge ->
            pckge.isDelivered
        }

        DeliveredPackages(
            dialogState = deliveredPackagesState,
            deliveredPackages = deliveredPackages
        )
    }
}

