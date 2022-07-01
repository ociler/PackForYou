package com.packforyou.ui.packages;

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.Circle
import com.packforyou.R
import com.packforyou.data.models.*
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White
import androidx.compose.ui.graphics.vector.VectorProperty.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@SuppressLint("UnrememberedMutableState")
@Composable
fun Packages(
    packagesViewModel: IPackagesViewModel
) {

    val packages = listOf(
        Package(
            location = Location(
                address = "Avd Universitat 44 Valencia Espanya"
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

    val columnHeightInPx = mutableStateOf(0)

    Column(Modifier.fillMaxHeight(.9f)) {

        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .weight(1f)
        ) {

            Divider(
                thickness = 5.dp,
                color = Color.Black,
                modifier = Modifier
                    .padding(
                        start = 60.dp, end = 60.dp,
                        top = 20.dp, bottom = 22.dp
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

                item {

                    Box {


                        //We are drawing the line taking into account the list height
                        Canvas(
                            modifier = Modifier
                                .height( with(LocalDensity.current) { columnHeightInPx.value.toDp() })
                        ) {

                            val height = size.height

                            drawLine(
                                start = Offset(x = 13f, y = 45f),
                                end = Offset(x = 13f, y = height),
                                color = Color.Black,
                                strokeWidth = 6f,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(30f, 20f), phase = 0f
                                )
                            )
                        }

                        Column(modifier = Modifier.onGloballyPositioned {
                            //we get the height in px of the column when it is already composed.
                            //It is a callback, so we need to use a mutableState
                            columnHeightInPx.value = it.size.height
                        }) {
                            packages.forEach { pckge ->

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Canvas(
                                        modifier = Modifier.size(10.dp),
                                        onDraw = {
                                            drawCircle(color = Black)
                                        }
                                    )

                                    Text(
                                        text = "REF ${pckge.numPackage}",
                                        style = PackForYouTypography.displayLarge,
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }

                                Spacer(Modifier.width(10.dp))

                                Column {
                                    Spacer(Modifier.height(15.dp))

                                    PackageItem(pckge = pckge)

                                    Spacer(modifier = Modifier.height(35.dp))
                                }
                            }
                        }

                    }
                }

            }
        }

        StartRouteRectangularButton()
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
fun StartRouteRectangularButton() {
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

