package com.packforyou.ui.packages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.packforyou.R
import com.packforyou.data.models.*
import com.packforyou.navigation.ArgumentsHolder
import com.packforyou.ui.theme.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PackageItem(pckge: Package, packages: MutableState<List<Package>>? = null) {
    val dismissState = rememberDismissState(
        initialValue = DismissValue.Default,
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                //TODO remove package
                ArgumentsHolder.packagesList = ArgumentsHolder.packagesList.minusElement(pckge)
                packages!!.value = ArgumentsHolder.packagesList

            } else if (it == DismissValue.DismissedToEnd) {
                pckge.isDelivered = true
            }
            true
        }
    )

    Column(
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        SwipeToDismiss(
            state = dismissState,
            dismissThresholds = { FractionalThreshold(0.4f) },
            background = {
                val color = when (dismissState.dismissDirection) {
                    DismissDirection.StartToEnd, DismissDirection.EndToStart -> Black
                    null -> Color.Transparent
                }

                val direction = dismissState.dismissDirection

                Surface(shape = RoundedCornerShape(25.dp)) {
                    if (direction == DismissDirection.StartToEnd) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(end = 20.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_mark_as_delivered),
                                    contentDescription = "Marks a package as delivered",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(32.dp),
                                )

                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(start = 20.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delete),
                                    contentDescription = "Deletes the package",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(32.dp)
                                )
                            }

                        }

                    }
                }
            },
            modifier = Modifier
                .background(Color.Transparent)
                .wrapContentWidth()

        ) {
            PackageCard(pckge = pckge)
        }

        Spacer(modifier = Modifier.height(12.dp))

        StateIcon(
            state = pckge.state,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageCard(pckge: Package) {

    val color = if (pckge.state == PackageState.POSTPONED_DELIVERY)
        PostponedCard
    else
        White

    Surface(
        elevation = 10.dp,
        color = color, shape = RoundedCornerShape(25.dp)
    ) {
        Card(
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Column(
                Modifier
                    .padding(15.dp)
                    .wrapContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pckge.location.address,
                            style = PackForYouTypography.bodyMedium
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = pckge.client.name,
                            style = PackForYouTypography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    UrgencyIcon(
                        urgency = pckge.urgency
                    )
                }
                if (!pckge.note.isNullOrBlank()) { //TODO correct the little card under the card.
                    // idk why because of this block of code this little card appears always.
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
        modifier =
        modifier
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
            modifier = modifier,
            style = PackForYouTypography.bodyMedium,
            maxLines = 1
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplePackageItem(pckge: Package, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "REF ${pckge.numPackage}",
            style = PackForYouTypography.displayLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 5.dp)
        )
        Spacer(Modifier.width(10.dp))
        Surface(
            shadowElevation = 10.dp,
            color = White, shape = RoundedCornerShape(25.dp)
        ) {
            Card(
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                modifier = Modifier.wrapContentSize()
            ) {
                Column(
                    Modifier
                        .padding(15.dp)
                ) {
                    Column {
                        Text(
                            text = pckge.location.address,
                            style = PackForYouTypography.bodyMedium
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = pckge.client.name,
                            style = PackForYouTypography.bodyMedium
                        )

                        Spacer(Modifier.height(5.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Spacer(
                                Modifier.weight(1f)
                            )
                            UrgencyIcon(
                                urgency = pckge.urgency
                            )
                        }
                    }

                }
            }
        }
    }
}