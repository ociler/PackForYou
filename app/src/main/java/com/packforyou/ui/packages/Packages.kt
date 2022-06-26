package com.packforyou.ui.packages;

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.packforyou.R
import com.packforyou.data.models.*
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.NotConfirmed
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White

@Composable
fun Packages(
    packagesViewModel: IPackagesViewModel
) {

    val packages = listOf(
        Package(
            location = Location(
                address = "Avd Universitat 44"
            ),
            client = Client(name = "Esther Frasquet"),
            urgency = Urgency.URGENT,
            state = PackageState.NEW_LOCATION
        ),

        Package(
            location = Location(
                address = "Carrer Arquitecte Arnau 30, Valencia"
            ),
            client = Client(name = "Esther Frasquet"),
            note = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown.",
            state = PackageState.CONFIRMED
        ),

        Package(
            location = Location(
                address = "Avd Universitat 44"
            ),
            client = Client(name = "Esther Frasquet"),
            state = PackageState.POSTPONED_DELIVERY
        ),

        Package(
            location = Location(
                address = "Avd Universitat 44"
            ),
            client = Client(name = "Esther Frasquet"),
            urgency = Urgency.VERY_URGENT,
            state = PackageState.NOT_CONFIRMED
        ),

        Package(
            location = Location(
                address = "Avd Universitat 44"
            ),
            client = Client(name = "Esther Frasquet"),
            urgency = Urgency.VERY_URGENT,
            note = "olei",
            state = PackageState.NOT_CONFIRMED
        )
    )

    Column {

        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .weight(1f),

            ) {

            Divider(
                thickness = 5.dp,
                color = Color.Black,
                modifier = Modifier
                    .padding(
                        start = 60.dp, end = 60.dp,
                        top = 20.dp, bottom = 45.dp
                    )
                    .clip(RoundedCornerShape(50.dp))
            )

            LazyColumn(
                modifier = Modifier
                    .background(Color.Transparent)
            ) {

                item {
                    FilterButton()
                }
                items(packages) { pckge ->
                    PackageItem(pckge = pckge)
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
        }

        StartRouteButton()
    }
}

@Composable
fun FilterButton() {
    Row(Modifier.padding(end = 5.dp, bottom = 25.dp)) {
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clickable {
                    println("Sorting packages")
                }
                .clip(RoundedCornerShape(10.dp))
                .background(Black)
                .padding(5.dp)
                .shadow(5.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sort),
                contentDescription = "Sorts packages",
                tint = White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun StartRouteButton(){
    Button(
        onClick = {
            //TODO start route
            println("Starting route")
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
                text = "Start Route",
                color = White,
                style = PackForYouTypography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_navigation),
            contentDescription = "Starts the navigation",
            tint = White
        )
    }
}

