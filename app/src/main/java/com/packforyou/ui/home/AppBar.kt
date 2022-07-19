package com.packforyou.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.packforyou.R
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.packages.DeliveredPackages
import com.packforyou.ui.packages.IPackagesViewModel
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White


@Composable
fun AppBar(
    navigationIcon: ImageVector,
    onNavigationIconClick: () -> Unit,
    packagesViewModel: IPackagesViewModel
) {

    val deliveredPackagesState = remember {
        mutableStateOf(false)
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(
                    id = R.string.app_name
                ),
                textAlign = TextAlign.Center,
                style = PackForYouTypography.headlineMedium
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

