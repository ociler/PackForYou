package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import com.packforyou.navigation.SetupNavGraph
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.atlas.IAtlasViewModel
import com.packforyou.ui.home.*
import com.packforyou.ui.login.ILoginViewModel
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.packages.*
import com.packforyou.ui.theme.PackForYouTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStreamReader
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var loginViewModel: ILoginViewModel
    private lateinit var packagesViewModel: IPackagesViewModel
    private lateinit var atlasViewModel: IAtlasViewModel

    private lateinit var notOptimizedRoute: Route

    private lateinit var bruteForceTravelTimeRoute: Route
    private lateinit var closestNeighbourTravelTimeRoute: Route

    private lateinit var bruteForceDistanceRoute: Route
    private lateinit var closestNeighbourDistanceRoute: Route

    private lateinit var optimizedDirectionsAPI: Route


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this)[LoginViewModelImpl::class.java]

        packagesViewModel =
            ViewModelProvider(this)[PackagesViewModelImpl::class.java]

        atlasViewModel =
            ViewModelProvider(this)[AtlasViewModelImpl::class.java]

        var t0: Long = 0
        var t1: Long


        val mode = "Town" //with this, we will control which situation we want to test
        val numPckgMode = "5"
        val usingLocalFiles = true
        val callingDirectionsAPI = false
        val testingAlgorithms = false


        //we have some json files with the locations and packages decoded. We will use them
        val startLocation = restoreStartLocation(mode)
        val lastLocation = restoreLastLocation(mode)

        val packages = restorePackagesFromJson(mode + numPckgMode).toList()

        setContent {
            PackForYouTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    viewModelOwner = this,
                    lifecycleOwner = this
                )
            }
        }

        packages.forEachIndexed { i, it ->
            it.position = i
        }

        notOptimizedRoute =
            Route(packages = packages, startLocation = startLocation, endLocation = lastLocation)

        if(testingAlgorithms) {

            if (usingLocalFiles) {
                //We have already saved our arrays from a previous run of the app. In order to make less calls,
                // we will use them instead of making calls everytime we execute the app
                /***USING LOCAL FILES****/
                restoreArraysFromJson(mode + numPckgMode)
            } else {
                //first we fill all the distances arrays. This function, when it finishes, will call the one to get endArray and, when it finishes, it will call the one to get the array between all packages
                //Here we need the endLocation as well because we have no other way to get this Location. Maybe it is a bit strange because of the name
                /*****CALLING API*****/

                t0 = System.currentTimeMillis()
                packagesViewModel.computeDistanceBetweenStartLocationAndPackages(
                    startLocation,
                    lastLocation,
                    packages
                )
            }


            packagesViewModel.observeTravelTimeArray()
                .observe(this) { travelTimeArray ->

                    if (!usingLocalFiles) {
                        t1 = System.currentTimeMillis()

                        println("TIME TO GET ARRAYS FROM MATRIX API: ${t1 - t0} ms")
                        saveArraysInJson()
                    }

                    /****NOT OPTIMIZED****/
                    notOptimizedRoute.totalTime = packagesViewModel.getRouteTravelTime(
                        notOptimizedRoute.packages,
                        travelTimeArray,
                        packagesViewModel.getStartTravelTimeArray(),
                        packagesViewModel.getEndTravelTimeArray()
                    )

                    notOptimizedRoute.totalDistance = packagesViewModel.getRouteDistance(
                        notOptimizedRoute.packages,
                        packagesViewModel.getDistanceArray(),
                        packagesViewModel.getStartDistanceArray(),
                        packagesViewModel.getEndDistanceArray()
                    )

                    /****BRUTE FORCE****/

                    packagesViewModel.computePermutations(notOptimizedRoute.packages.size)

                    val bruteForceTravelTimeTime = measureTimeMillis {
                        bruteForceTravelTimeRoute =
                            packagesViewModel.getOptimizedRouteBruteForceTravelTime(
                                notOptimizedRoute,
                                travelTimeArray,
                                packagesViewModel.getStartTravelTimeArray(),
                                packagesViewModel.getEndTravelTimeArray()
                            ) //TODO maybe this should be in another thread, as it will take some time (theoretically)
                    }

                    val bruteForceDistanceTime = measureTimeMillis {
                        bruteForceDistanceRoute =
                            packagesViewModel.getOptimizedRouteBruteForceDistance(
                                notOptimizedRoute,
                                packagesViewModel.getDistanceArray(),
                                packagesViewModel.getStartDistanceArray(),
                                packagesViewModel.getEndDistanceArray()
                            ) //TODO maybe this should be in another thread, as it will take some time (theoretically)
                    }

                    /****CLOSEST NEIGHBOUR ****/
                    val closestNeighbourTravelTimeTime = measureNanoTime {
                        closestNeighbourTravelTimeRoute =
                            packagesViewModel.getOptimizedRouteClosestNeighbourTravelTime(
                                notOptimizedRoute,
                                travelTimeArray,
                                packagesViewModel.getStartTravelTimeArray(),
                                packagesViewModel.getEndTravelTimeArray()
                            )
                    }

                    val closestNeighbourDistanceTime = measureNanoTime {
                        closestNeighbourDistanceRoute =
                            packagesViewModel.getOptimizedRouteClosestNeighbourDistance(
                                notOptimizedRoute,
                                packagesViewModel.getDistanceArray(),
                                packagesViewModel.getStartDistanceArray(),
                                packagesViewModel.getEndDistanceArray()
                            )
                    }

                    println("RESULTS")
                    println("------------------------------\n")

                    println("------------------------------")
                    println("Not Optimized route: ")
                    println("Algorithm time: 0ns")
                    println("------------------------------")

                    println("Starting point: $startLocation")
                    notOptimizedRoute.packages.forEachIndexed { index, pckg ->
                        println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                    }

                    println("Ending point: $lastLocation")
                    println("Total travel time: ${notOptimizedRoute.totalTime} seconds")
                    println("Total distance: ${notOptimizedRoute.totalDistance} meters\n\n")


                    /***BRUTE FORCE***/
                    if (notOptimizedRoute.packages.size <= 10) {
                        println("--------------------------------------------")
                        println("Brute Force Optimized route by TRAVEL TIME: ")
                        println("Algorithm time: $bruteForceTravelTimeTime ms")
                        println("--------------------------------------------")

                        savePackagesInJson(bruteForceTravelTimeRoute.packages.toList())

                        println("Starting point: $startLocation")
                        bruteForceTravelTimeRoute.packages.forEachIndexed { index, pckg ->
                            println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                        }

                        println("Ending point: $lastLocation")
                        println("Total travel time: ${bruteForceTravelTimeRoute.totalTime} seconds\n\n")


                        println("-----------------------------------------")
                        println("Brute Force Optimized route by DISTANCE: ")
                        println("Algorithm time: $bruteForceDistanceTime ms")
                        println("-----------------------------------------")

                        savePackagesInJson(bruteForceDistanceRoute.packages.toList())

                        println("Starting point: $startLocation")
                        bruteForceDistanceRoute.packages.forEachIndexed { index, pckg ->
                            println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                        }

                        println("Ending point: $lastLocation")
                        println("Total distance: ${bruteForceDistanceRoute.totalDistance} meters\n\n")

                    }

                    /***CLOSEST NEIGHBOUR***/
                    if (closestNeighbourDistanceRoute.packages.size % 5 == 0 || closestNeighbourDistanceRoute.packages.size == 4) { //not print from 6 to 9

                        println("--------------------------------------------------")
                        println("Closest neighbour Optimized route by TRAVEL TIME: ")
                        println("Algorithm time: $closestNeighbourTravelTimeTime ns")
                        println("--------------------------------------------------")

                        savePackagesInJson(closestNeighbourTravelTimeRoute.packages.toList())

                        println("Starting point: $startLocation")
                        closestNeighbourTravelTimeRoute.packages.forEachIndexed { index, pckg ->
                            println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                        }
                        println("Ending point: $lastLocation")
                        println("Total travel time: ${closestNeighbourTravelTimeRoute.totalTime} seconds\n\n")



                        println("-----------------------------------")
                        println("Closest neighbour Optimized route by DISTANCE: ")
                        println("Algorithm time: $closestNeighbourDistanceTime ns")
                        println("-----------------------------------")

                        savePackagesInJson(bruteForceDistanceRoute.packages.toList())

                        println("Starting point: $startLocation")
                        closestNeighbourDistanceRoute.packages.forEachIndexed { index, pckg ->
                            println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                        }
                        println("Ending point: $lastLocation")
                        println("Total distance: ${closestNeighbourDistanceRoute.totalDistance} meters\n\n")
                    }
                }


            /****GOOGLE MAPS OPTIMIZATION****/

            if (callingDirectionsAPI) {

                val directionsAPITime = measureTimeMillis {
                    packagesViewModel.computeOptimizedRouteDirectionsAPI(notOptimizedRoute)
                }


                packagesViewModel.observeOptimizedDirectionsAPIRoute()
                    .observe(this) { directionsRoute ->
                        optimizedDirectionsAPI = directionsRoute

                        println("-----------------------------------")
                        println("Directions API Optimized route: ")
                        println("Algorithm time: $directionsAPITime ms")
                        println("-----------------------------------")

                        savePackagesInJson(optimizedDirectionsAPI.packages.toList())

                        println("Starting point: $startLocation")
                        optimizedDirectionsAPI.packages.forEachIndexed { index, pckg ->
                            println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                        }

                        println("Ending point: $lastLocation")
                        println("Total travel time: ${directionsRoute.totalTime} seconds")
                        println("Total distance: ${directionsRoute.totalDistance} meters\n\n")
                    }

                val locations = arrayListOf(startLocation)

                packages.forEach {
                    locations.add(it.location)
                }
                locations.add(lastLocation)
            }
        }

    }

    private fun saveArraysInJson() {
        println("TRAVEL TIME ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.observeTravelTimeArray().value))
        println("--------------------\n\n")

        println("DISTANCE ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getDistanceArray()))
        println("--------------------\n\n")


        println("START TRAVEL TIME ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getStartTravelTimeArray()))
        println("--------------------\n\n")

        println("START DISTANCE ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getStartDistanceArray()))
        println("--------------------\n\n")

        println("END TRAVEL TIME ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getEndTravelTimeArray()))
        println("--------------------\n\n")

        println("END DISTANCE ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getEndDistanceArray()))
        println("--------------------\n\n")
    }

    private fun savePackagesInJson(
        packages: List<Package>
    ) {
        println(Gson().toJson(packages))
    }

    private fun restoreArraysFromJson(mode: String) {
        val gson = Gson()
        var reader: InputStreamReader

        when (mode) {
            "Town5" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town5))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town5))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town5))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town5))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town5))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town5))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town6" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town6))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town6))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town6))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town6))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town6))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town6))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town7" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town7))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town7))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town7))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town7))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town7))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town7))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town8" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town8))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town8))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town8))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town8))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town8))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town8))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town9" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town9))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town9))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town9))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town9))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town9))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town9))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town10" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town10))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town10))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town10))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town10))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town10))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town10))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town15" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town15))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town15))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town15))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town15))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town15))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town15))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town20" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town20))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town20))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town20))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town20))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town20))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town20))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town25" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town25))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town25))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town25))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town25))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town25))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town25))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }


            "City5" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city5))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city5))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city5))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city5))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city5))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city5))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City6" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city6))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city6))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city6))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city6))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city6))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city6))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City7" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city7))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city7))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city7))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city7))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city7))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city7))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City8" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city8))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city8))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city8))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city8))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city8))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city8))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City9" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city9))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city9))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city9))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city9))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city9))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city9))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City10" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city10))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city10))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city10))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city10))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city10))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city10))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City15" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city15))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city15))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city15))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city15))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city15))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city15))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }


            "City20" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city20))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city20))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city20))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city20))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city20))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city20))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City25" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city25))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city25))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city25))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city25))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city25))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city25))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            else -> println("Sorry, not valid mode given")
        }
    }

    private fun restorePackagesFromJson(mode: String): Array<Package> {
        var packages = arrayOf<Package>()
        val gson = Gson()
        val reader: InputStreamReader

        when (mode) {

            "Town5" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages5))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town6" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages6))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town7" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages7))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town8" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages8))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town9" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages9))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town10" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages10))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town15" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages15))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town20" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages20))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town25" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages25))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }


            "City5" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages5))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }


            "City6" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages6))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }


            "City7" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages7))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }


            "City8" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages8))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City9" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages9))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City10" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages10))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City15" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages15))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City20" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages20))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City25" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages25))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            else -> {
                println("Sorry, no valid mode given")
            }
        }
        return packages
    }

    private fun restoreStartLocation(mode: String): Location {
        var startLocation = Location()
        when (mode) {

            "City" -> {
                val reader =
                    InputStreamReader(resources.openRawResource(R.raw.city_start_location))
                startLocation = Gson().fromJson(reader, Location::class.java)
            }

            "Town" -> {
                val reader = InputStreamReader(resources.openRawResource(R.raw.town_start_location))
                return Gson().fromJson(reader, Location::class.java)
            }

            else -> {
                println("Sorry, no valid mode given")
            }
        }
        return startLocation
    }

    private fun restoreLastLocation(mode: String): Location {
        var endLocation = Location()
        when (mode) {

            "City" -> {
                val reader =
                    InputStreamReader(resources.openRawResource(R.raw.city_end_location))
                endLocation = Gson().fromJson(reader, Location::class.java)
            }

            "Town" -> {
                val reader = InputStreamReader(resources.openRawResource(R.raw.town_end_location))
                return Gson().fromJson(reader, Location::class.java)
            }

            else -> {
                println("Sorry, no valid mode given")
            }
        }
        return endLocation
    }
}