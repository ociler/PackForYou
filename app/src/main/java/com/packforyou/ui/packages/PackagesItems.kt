package com.packforyou.ui.packages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.packforyou.R
import com.packforyou.data.models.*
import com.packforyou.ui.theme.*


@Preview
@Composable
fun PackageItemPreview() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 25.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        PackageItem(
            pckge = Package(
                location = Location(
                    address = "Avd Universitat 44"
                ),
                client = Client(name = "Esther Frasquet"),
                urgency = Urgency.URGENT,
                state = PackageState.NEW_LOCATION
            )
        )

        PackageItem(
            pckge = Package(
                location = Location(
                    address = "Carrer Arquitecte Arnau 30, Valencia"
                ),
                client = Client(name = "Esther Frasquet"),
                note = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown.",
                state = PackageState.CONFIRMED
            )
        )

        PackageItem(
            pckge = Package(
                location = Location(
                    address = "Avd Universitat 44"
                ),
                client = Client(name = "Esther Frasquet"),
                state = PackageState.POSTPONED_DELIVERY
            )
        )

        PackageItem(
            pckge = Package(
                location = Location(
                    address = "Avd Universitat 44"
                ),
                client = Client(name = "Esther Frasquet"),
                urgency = Urgency.VERY_URGENT,
                state = PackageState.NOT_CONFIRMED
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageItem(pckge: Package) {
    Column {
        Card(
            shape = RoundedCornerShape(25.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(containerColor = White)

        ) {
            Column(
                Modifier.padding(15.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pckge.location.address,
                            fontWeight = FontWeight.Bold,
                            style = PackForYouTypography.bodyMedium
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = pckge.client!!.name,
                            fontWeight = FontWeight.Bold,
                            style = PackForYouTypography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.width(30.dp))

                    UrgencyIcon(
                        urgency = pckge.urgency
                    )

                }
                if (!pckge.note.isNullOrBlank()) {

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_package_note),
                            "Package note"
                        )

                        Spacer(modifier = Modifier.width(15.dp))

                        Text(
                            text = pckge.note!!,
                            textAlign = TextAlign.Justify,
                            style = PackForYouTypography.bodyMedium
                        )

                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        StateIcon(state = pckge.state)
    }
}

@Composable
fun UrgencyIcon(urgency: Urgency?, modifier: Modifier = Modifier) {
    val caption = when (urgency) {
        Urgency.URGENT -> "Express Delivery"
        Urgency.VERY_URGENT -> "Urgent Delivery"
        else -> "Standard Delivery"
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Color.Black)
    ) {
        Text(
            text = caption,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 5.dp),
            style = PackForYouTypography.bodyMedium,
            maxLines = 1
        )

    }
}

@Composable
fun StateIcon(state: PackageState, modifier: Modifier = Modifier) {
    val caption: String
    val color: Color
    when (state) {
        PackageState.NOT_CONFIRMED -> {
            caption = "Not confirmed"
            color = NotConfirmed
        }
        PackageState.CONFIRMED -> {
            caption = "Confirmed"
            color = Confirmed
        }

        PackageState.POSTPONED_DELIVERY -> {
            caption = "Cancelled"
            color = Cancelled
        }

        PackageState.NEW_LOCATION -> {
            caption = "New Location"
            color = NewLocation
        }
        else -> {
            caption = "Message not send"
            color = NotConfirmed
        }
    }

    Box(
        Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(color)
    ) {
        Text(
            text = caption,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 5.dp),
            style = PackForYouTypography.bodyMedium,
            maxLines = 1
        )

    }
}