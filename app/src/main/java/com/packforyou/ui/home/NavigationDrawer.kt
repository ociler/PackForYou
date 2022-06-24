package com.packforyou.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.packforyou.R
import com.packforyou.ui.theme.PackForYouTypography
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerHeader(scaffoldState: BottomSheetScaffoldState) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Closes the Drawer",
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterStart)
                .clickable {
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
        )

        Text(
            text = "PackForYou",
            fontSize = 25.sp,
            textAlign = TextAlign.Right,
            style = PackForYouTypography.displayMedium,
            modifier = Modifier
                .padding(end = 20.dp)
                .align(Alignment.CenterEnd)
        )
    }
    Divider(color = Color.Black, thickness = 1.dp)
}

@Composable
fun DrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (MenuItem) -> Unit
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(12.dp)
            ) {
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Divider(color = Color.Black, thickness = 1.dp)
        }
    }
}

@Composable
fun DrawerFooter(modifier: Modifier = Modifier) {
    Divider(color = Color.Black, thickness = 1.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                println("Logged out") //TODO log out function
            }
            .padding(12.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Text(
                text = stringResource(id = R.string.log_out)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_log_out),
                contentDescription = "Logs out from de session",
                modifier = Modifier
                    .padding(start = 10.dp)
            )
        }
    }
}

