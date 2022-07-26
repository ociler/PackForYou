package com.packforyou.ui.packages;

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.packforyou.R
import com.packforyou.data.models.*
import com.packforyou.navigation.Screen
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.theme.Black
import com.packforyou.ui.theme.PackForYouTypography
import com.packforyou.ui.theme.White

lateinit var expanded: MutableState<Boolean>

@Composable
fun PackagesScreen(
    navController: NavController,
    packagesViewModel: IPackagesViewModel,
    packages: MutableState<List<Package>>,
    lifecycleOwner: LifecycleOwner
) {

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
                    FilterButton(packagesViewModel, lifecycleOwner)
                }

                //we need the key value because if we don't use it, the column will recognize each
                //element by his position on the column and, if we remove it, the column will be confused
                //and will mark as dismissed the element below of it because the column thinks that it is
                //this one the dismissed one as it's on the position the removed element was.
                //With the key parameter, the lazy column will take the numPackage as the way
                //to recognize each element
                itemsIndexed(packages.value, key = { _: Int, pckge: Package ->
                    pckge.numPackage
                }) { index, pckge ->

                    val columnHeightInPx = remember {
                        mutableStateOf(0)
                    }

                    Box {
                        //Pointed line
                        val modifier = if (index != packages.value.lastIndex) {
                            Modifier.height(with(LocalDensity.current) { columnHeightInPx.value.toDp() })
                        } else {
                            Modifier
                        }

                        Canvas(
                            modifier = modifier
                        ) {

                            val height = size.height

                            drawLine(
                                start = Offset(x = 13f, y = 45f),
                                end = Offset(x = 13f, y = height + 30f),
                                color = Color.Black,
                                strokeWidth = 6f,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(30f, 20f), phase = 0f
                                )
                            )
                        }

                        Column(modifier = Modifier.onGloballyPositioned {
                            //we get the height in px of the column (item) when it is already composed.
                            //it is a callback so it needs to be a mutableState
                            columnHeightInPx.value = it.size.height
                        }) {


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

                                PackageItem(pckge = pckge, viewModel = packagesViewModel)

                                Spacer(modifier = Modifier.height(35.dp))
                            }
                        }

                    }
                }
            }
        }

        StartRouteRectangularButton(navController)
    }
}

@Composable
fun FilterButton(viewModel: IPackagesViewModel, lifecycleOwner: LifecycleOwner) {

    expanded = remember { mutableStateOf(false) }
    var selectedAlgorithm by remember { mutableStateOf(Algorithm.NOT_ALGORITHM) }

    var context = LocalContext.current

    val algorithmOptions = listOf(
        "Directions API",
        "Brute Force",
        "Closest Neighbour"
    )

    Row(Modifier.padding(end = 5.dp, bottom = 25.dp)) {
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Black)
                .padding(5.dp)
                .shadow(5.dp)
        ) {
            IconButton(
                onClick = {
                    expanded.value = true
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = "Sorts packages",
                    tint = White,
                    modifier = Modifier.fillMaxSize()
                )
            }

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                algorithmOptions.forEach {
                    DropdownMenuItem(
                        onClick = {
                            selectedAlgorithm = getAlgorithmGivenString(it)
                            computeProperAlgorithmAndUpdateRoute(
                                selectedAlgorithm,
                                viewModel,
                                lifecycleOwner,
                                context
                            )
                            expanded.value = false

                        }, text = {
                            Text(text = it)
                        })
                }
            }
        }
    }
}

@Composable
fun StartRouteRectangularButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate(route = Screen.StartRoute.route)
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

private fun getAlgorithmGivenString(algorithmString: String): Algorithm {
    return when (algorithmString) {
        "Directions API" -> {
            Algorithm.DIRECTIONS_API
        }

        "Brute Force" -> {
            Algorithm.DIRECTIONS_API
        }

        "Closest Neighbour" -> {
            Algorithm.CLOSEST_NEIGHBOUR
        }

        else -> {
            Algorithm.NOT_ALGORITHM
        }
    }
}

private fun computeProperAlgorithmAndUpdateRoute(
    algorithm: Algorithm,
    viewModel: IPackagesViewModel,
    owner: LifecycleOwner,
    context: Context
) {
    when (algorithm) {
        Algorithm.DIRECTIONS_API -> {
            viewModel.computeOptimizedRouteDirectionsAPI(CurrentSession.route.value)
            viewModel.observeOptimizedDirectionsAPIRoute().observe(owner) { route ->
                CurrentSession.route.value = route
                CurrentSession.packagesToDeliver.value = route.packages
            }
        }

        Algorithm.BRUTE_FORCE -> {
            if (CurrentSession.route.value.packages.size <= 10) {
                viewModel
            } else {
                Toast.makeText(
                    context,
                    "Sorry, we can't compute Brute Force Algorithm with more than 10 packages",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        Algorithm.CLOSEST_NEIGHBOUR -> {

        }
    }
}