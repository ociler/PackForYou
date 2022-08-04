package com.packforyou.ui.packages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.packforyou.R
import com.packforyou.data.models.*
import com.packforyou.navigation.Screen
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.theme.*

lateinit var expanded: MutableState<Boolean>
lateinit var selectedAlgorithm: MutableState<Algorithm>

val algorithmOptions = listOf(
    "Directions API",
    "Brute Force",
    "Closest Neighbour",
    "Urgency",
    "Custom Sort"
)

@Composable
fun PackagesScreen(
    navController: NavController,
    packagesViewModel: IPackagesViewModel,
    packages: MutableState<List<Package>>,
    lifecycleOwner: LifecycleOwner
) {

    IsLoading.state = remember { mutableStateOf(false) }
    expanded = remember { mutableStateOf(false) }
    selectedAlgorithm = remember {
        mutableStateOf(CurrentSession.algorithm)
    }

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ordered by: ",
                            style = PackForYouTypography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.weight(1f))

                        if (!expanded.value) {
                            EnabledAlgorithmItem(
                                algorithmName = getStringGivenAlgorithm(
                                    CurrentSession.algorithm
                                )
                            )

                            Spacer(modifier = Modifier.width(5.dp))
                        }

                        if (expanded.value) {
                            Spacer(modifier = Modifier.width(10.dp))
                            UpsideDownTriangle(
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 10.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(5.dp))
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                SidedTriangle(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .rotate(90f)
                                        .padding(end = 10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(5.dp))
                        }
                        FilterButton()
                        Spacer(Modifier.padding(end = 5.dp))
                    }

                    Spacer(Modifier.padding(bottom = 20.dp))
                }

                if (expanded.value) {
                    item {
                        Spacer(modifier = Modifier.height(0.dp))
                        LazyRow {
                            items(algorithmOptions) { algorithmName ->
                                if (getAlgorithmGivenString(algorithmName) == CurrentSession.algorithm) {
                                    EnabledAlgorithmItem(
                                       algorithmName = algorithmName
                                    )
                                } else {
                                    DisabledAlgorithmItem(
                                        algorithmName = algorithmName,
                                        viewModel = packagesViewModel,
                                        lifecycleOwner = lifecycleOwner
                                    )
                                }

                                Spacer(modifier = Modifier.width(15.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(25.dp))
                    }
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

        Row(
            Modifier
                .fillMaxWidth()
                .background(color = Color(alpha = .05f, red = 0f, green = 0f, blue = 0f))
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Inter,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    ) {
                        append("Estimated time: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Inter,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("${CurrentSession.travelTime.value / 60} min")
                    }
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            )
        }
        StartRouteRectangularButton(navController, packages.value.isEmpty())
    }
}

@Composable
fun UpsideDownTriangle(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.aspectRatio(1f)
    ) {
        val rect = Rect(Offset.Zero, size)
        val trianglePath = Path().apply {
            moveTo(rect.bottomCenter.x, rect.bottomCenter.y)
            lineTo(rect.topRight.x, rect.topRight.y)
            lineTo(rect.topLeft.x, rect.topLeft.y)
            close()
        }

        drawIntoCanvas { canvas ->
            canvas.drawOutline(
                outline = Outline.Generic(trianglePath),
                paint = Paint().apply {
                    color = Black
                    pathEffect = PathEffect.cornerPathEffect(rect.maxDimension / 4)
                }
            )
        }
    }
}

@Composable
fun SidedTriangle(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .rotate(270f)
    ) {
        val rect = Rect(Offset.Zero, size)
        val trianglePath = Path().apply {
            moveTo(rect.centerLeft.x, rect.centerLeft.y)
            lineTo(rect.topRight.x, rect.topRight.y)
            lineTo(rect.bottomRight.x, rect.bottomRight.y)
            close()
        }

        drawIntoCanvas { canvas ->
            canvas.drawOutline(
                outline = Outline.Generic(trianglePath),
                paint = Paint().apply {
                    color = Black
                    pathEffect = PathEffect.cornerPathEffect(rect.maxDimension / 4)
                }
            )
        }
    }
}

@Composable
fun EnabledAlgorithmItem(
    algorithmName: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Black
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = algorithmName,
                    color = Color.White,
                    style = PackForYouTypography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun DisabledAlgorithmItem(
    algorithmName: String,
    viewModel: IPackagesViewModel,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = White,
        border = BorderStroke(width = 2.dp, color = Black)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    selectedAlgorithm.value = getAlgorithmGivenString(algorithmName)
                    IsLoading.state.value = true

                    computeProperAlgorithmAndUpdateRoute(
                        selectedAlgorithm.value,
                        viewModel,
                        lifecycleOwner,
                        context
                    )

                    expanded.value = false
                }
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = algorithmName,
                    color = Black,
                    style = PackForYouTypography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@Composable
fun FilterButton() {

    Row {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Black)
                .padding(5.dp)
                .shadow(5.dp)
        ) {
            IconButton(
                onClick = {
                    expanded.value = !expanded.value
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
        }
    }
}

@Composable
fun StartRouteRectangularButton(navController: NavController, isEmpty: Boolean) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (isEmpty) {
                Toast.makeText(
                    context,
                    "You have no packages to deliver, so you can't Start a route.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                navController.navigate(route = Screen.StartRoute.route)
            }
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
            Algorithm.BRUTE_FORCE
        }

        "Closest Neighbour" -> {
            Algorithm.CLOSEST_NEIGHBOUR
        }

        "Urgency" -> {
            Algorithm.URGENCY
        }

        else -> {
            Algorithm.NOT_ALGORITHM
        }
    }
}

private fun getStringGivenAlgorithm(algorithm: Algorithm): String {
    return when (algorithm) {
        Algorithm.DIRECTIONS_API -> {
            "Directions API"
        }

        Algorithm.BRUTE_FORCE -> {
            "Brute Force"
        }

        Algorithm.CLOSEST_NEIGHBOUR -> {
            "Closest Neighbour"
        }

        Algorithm.URGENCY -> {
            "Urgency"
        }

        else -> {
            "Custom Algorithm"
        }
    }
}

//TODO refactor this method, as it has a lot of business logic that should be on the viewModel
private fun computeProperAlgorithmAndUpdateRoute(
    algorithm: Algorithm,
    viewModel: IPackagesViewModel,
    owner: LifecycleOwner,
    context: Context
) {
    var isFirstExec = true
    val route = CurrentSession.route.value
    val isLoading = IsLoading.state

    when (algorithm) {
        Algorithm.DIRECTIONS_API -> {
            viewModel.observeOptimizedDirectionsAPIRoute().removeObservers(owner)
            CurrentSession.algorithm = Algorithm.DIRECTIONS_API

            viewModel.computeOptimizedRouteDirectionsAPI(route)
            viewModel.observeOptimizedDirectionsAPIRoute().observe(owner) { optimizedRoute ->
                CurrentSession.route.value = optimizedRoute
                CurrentSession.packagesToDeliver.value = optimizedRoute.packages



                IsLoading.state.value = false
                Toast.makeText(context, "Packages sorted by Directions API.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        Algorithm.BRUTE_FORCE -> {

            if (CurrentSession.route.value.packages.size <= 10) {
                CurrentSession.algorithm = Algorithm.BRUTE_FORCE

                if (viewModel.observeTravelTimeArray().value == null) {

                    //I reset the position
                    route.packages.forEachIndexed { index, pckg ->
                        pckg.position = index
                    }
                }

                //I compute the permutations.
                // This is like this bc this was the same for both brute force algorithms
                val listWithPositions = mutableListOf<Byte>()
                route.packages.forEach {
                    listWithPositions.add(it.position.toByte())
                }


                //this way we can use this when we have removed/delivered a package
                val arrayToPermute = listWithPositions.toTypedArray()

                viewModel.computePermutationsOfArray(arrayToPermute)

                if (viewModel.observeTravelTimeArray().value == null) {

                    //I get the arrays
                    viewModel.computeDistanceBetweenStartLocationAndPackages(
                        startLocation = route.startLocation,
                        endLocation = route.endLocation,
                        packages = route.packages
                    )

                    //once they are ready, we compute de brute force algorithm
                    viewModel.observeTravelTimeArray().observe(owner) { travelTimeArray ->
                        val optimizedRoute = viewModel.getOptimizedRouteBruteForceTravelTime(
                            route = route,
                            travelTimeArray = travelTimeArray,
                            startTravelTimeArray = viewModel.getStartTravelTimeArray(),
                            endTravelTimeArray = viewModel.getEndTravelTimeArray()
                        )

                        isLoading.value = false
                        //and we update the route
                        CurrentSession.route.value = optimizedRoute
                        CurrentSession.packagesToDeliver.value = optimizedRoute.packages

                        Toast.makeText(
                            context,
                            "Packages sorted by Brute Force",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                } else { //we already have the arrays

                    val optimizedRoute = viewModel.getOptimizedRouteBruteForceTravelTime(
                        route = route,
                        travelTimeArray = viewModel.observeTravelTimeArray().value!!,
                        startTravelTimeArray = viewModel.getStartTravelTimeArray(),
                        endTravelTimeArray = viewModel.getEndTravelTimeArray()
                    )

                    //and we update the route
                    CurrentSession.route.value = optimizedRoute
                    CurrentSession.packagesToDeliver.value = optimizedRoute.packages

                    IsLoading.state.value = false

                    Toast.makeText(context, "Packages sorted by Brute Force", Toast.LENGTH_SHORT)
                        .show()
                }


            } else {
                IsLoading.state.value = false

                Toast.makeText(
                    context,
                    "Sorry, we can't compute Brute Force Algorithm with more than 10 packages",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        Algorithm.CLOSEST_NEIGHBOUR -> {
            CurrentSession.algorithm = Algorithm.CLOSEST_NEIGHBOUR

            if (viewModel.observeTravelTimeArray().value == null) {
                isLoading.value = true

                //I reset the position
                route.packages.forEachIndexed { index, pckg ->
                    pckg.position = index
                }
            }


            //I compute the permutations.
            // This is like this bc this was the same for both brute force algorithms
            val listWithPositions = mutableListOf<Byte>()
            route.packages.forEach {
                listWithPositions.add(it.position.toByte())
            }

            //this way we can use this when we have removed/delivered a package
            val arrayToPermute = listWithPositions.toTypedArray()

            viewModel.computePermutationsOfArray(arrayToPermute)

            if (viewModel.observeTravelTimeArray().value == null) {

                //I get the arrays
                viewModel.computeDistanceBetweenStartLocationAndPackages(
                    startLocation = route.startLocation,
                    endLocation = route.endLocation,
                    packages = route.packages
                )


                viewModel.observeTravelTimeArray().observe(owner) { travelTimeArray ->
                    val optimizedRoute = viewModel.getOptimizedRouteClosestNeighbourTravelTime(
                        route = route,
                        travelTimeArray = travelTimeArray,
                        startTravelTimeArray = viewModel.getStartTravelTimeArray(),
                        endTravelTimeArray = viewModel.getEndTravelTimeArray()
                    )

                    isLoading.value = false

                    //and we update the route
                    CurrentSession.route.value = optimizedRoute
                    CurrentSession.packagesToDeliver.value = optimizedRoute.packages

                    Toast.makeText(
                        context,
                        "Packages sorted by Closest Neighbour",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else { //we already have the arrays
                val optimizedRoute = viewModel.getOptimizedRouteClosestNeighbourTravelTime(
                    route = route,
                    travelTimeArray = viewModel.observeTravelTimeArray().value!!,
                    startTravelTimeArray = viewModel.getStartTravelTimeArray(),
                    endTravelTimeArray = viewModel.getEndTravelTimeArray()
                )

                //and we update the route
                CurrentSession.route.value = optimizedRoute
                CurrentSession.packagesToDeliver.value = optimizedRoute.packages

                IsLoading.state.value = false

                Toast.makeText(context, "Packages sorted by Closest Neighbour", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        Algorithm.URGENCY -> {
            CurrentSession.algorithm = Algorithm.URGENCY

            val veryUrgentPackages = mutableListOf<Package>()
            val urgentPackages = mutableListOf<Package>()
            val notUrgentPackages = mutableListOf<Package>()

            val currentPackages = route.packages

            currentPackages.forEach {
                when (it.urgency) {
                    Urgency.VERY_URGENT -> {
                        veryUrgentPackages.add(it)
                    }
                    Urgency.URGENT -> {
                        urgentPackages.add(it)
                    }
                    else -> {
                        notUrgentPackages.add(it)
                    }
                }
            }

            val veryUrgentRoute = route.copy(packages = veryUrgentPackages)
            val urgentRoute = route.copy(packages = urgentPackages)
            val notUrgentRoute = route.copy(packages = notUrgentPackages)


            //this will throw callbacks and, at the end, when the optimizedNotUrgentRoute is ready,
            //we will be able to get the others, as they will be also ready
            viewModel.computeOptimizedRouteDirectionsAPIWithUrgency(
                veryUrgentRoute = veryUrgentRoute,
                urgentRoute = urgentRoute,
                notUrgentRoute = notUrgentRoute
            )

            viewModel.observeOptimizedVeryUrgentRoute()
                .observe(owner) { optimizedVeryUrgentRoute ->

                    //TODO SOLVE THIS LIVEDATA THING. The observe is triggered twice: One at the beginning
                    //bc optimizedVeryUrgentRoute already has a value and another one
                    //when it should be triggered (when this object.value changes).
                    //This means that these lines of code are computed twice
                    val optimizedPackages = mutableListOf<Package>()
                    var totalTime = 0

                    optimizedPackages.addAll(optimizedVeryUrgentRoute.packages)
                    optimizedPackages.addAll(viewModel.getUrgentRoute().packages)
                    optimizedPackages.addAll(viewModel.getNotUrgentRoute().packages)

                    totalTime += optimizedVeryUrgentRoute.totalTime!!
                    totalTime += viewModel.getUrgentRoute().totalTime!!
                    totalTime += viewModel.getNotUrgentRoute().totalTime!!

                    //bc the issue with twice-triggered code. I should fix this in a better way
                    if (isFirstExec) {
                        isFirstExec = false
                    } else {
                        viewModel.observeOptimizedVeryUrgentRoute().removeObservers(owner)

                        CurrentSession.route.value =
                            route.copy(packages = optimizedPackages, totalTime = totalTime)
                        CurrentSession.packagesToDeliver.value = optimizedPackages
                        CurrentSession.travelTime.value = totalTime

                        IsLoading.state.value = false
                        Toast.makeText(context, "Packages sorted by Urgency", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
        }

        else -> {
            CurrentSession.algorithm = Algorithm.NOT_ALGORITHM

            IsLoading.state.value = false
        }
    }
}