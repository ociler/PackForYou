package com.packforyou.algorithmsperformancetest

import android.content.res.Resources
import androidx.lifecycle.LifecycleOwner
import com.packforyou.data.models.Route
import com.packforyou.ui.packages.IPackagesViewModel
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class AlgorithmPerformanceTest(
    private val usingLocalFiles: Boolean,
    private val callingDirectionsAPI: Boolean,
    mode: String,
    numPckgMode: String,
    resources: Resources,
    private val packagesViewModel: IPackagesViewModel,
    private val owner: LifecycleOwner,
) {

    private val jsonRestore = RestoreJsonEntities(mode, numPckgMode, resources, packagesViewModel)
    private val jsonSave = SaveJsonEntities(packagesViewModel)

    //we have some json files with the locations and packages decoded. We will use them
    val startLocation = jsonRestore.restoreStartLocation()
    private val lastLocation = jsonRestore.restoreLastLocation()

    val packages = jsonRestore.restorePackagesFromJson(

    ).toList()

    private var t0: Long = 0
    private var t1: Long = 0

    private val notOptimizedRoute =
        Route(packages = packages, startLocation = startLocation, endLocation = lastLocation)

    private lateinit var bruteForceTravelTimeRoute: Route
    private lateinit var nearestNeighbourTravelTimeRoute: Route

    private lateinit var bruteForceDistanceRoute: Route
    private lateinit var nearestNeighbourDistanceRoute: Route

    private lateinit var optimizedDirectionsAPI: Route

    fun performTests() {
        packages.forEachIndexed { i, it ->
            it.position = i
        }
        if (usingLocalFiles) {
            //We have already saved our arrays from a previous run of the app. In order to make less calls,
            // we will use them instead of making calls everytime we execute the app
            /***USING LOCAL FILES****/
            jsonRestore.restoreArraysFromJson()
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
            .observe(owner) { travelTimeArray ->

                if (!usingLocalFiles) {
                    t1 = System.currentTimeMillis()

                    println("TIME TO GET ARRAYS FROM MATRIX API: ${t1 - t0} ms")
                    jsonSave.saveArraysInJson()
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

                /****Nearest NEIGHBOUR ****/
                val nearestNeighbourTravelTimeTime = measureNanoTime {
                    nearestNeighbourTravelTimeRoute =
                        packagesViewModel.getOptimizedRouteNearestNeighbourTravelTime(
                            notOptimizedRoute,
                            travelTimeArray,
                            packagesViewModel.getStartTravelTimeArray(),
                            packagesViewModel.getEndTravelTimeArray()
                        )
                }

                val nearestNeighbourDistanceTime = measureNanoTime {
                    nearestNeighbourDistanceRoute =
                        packagesViewModel.getOptimizedRouteNearestNeighbourDistance(
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

                    jsonSave.savePackagesInJson(bruteForceTravelTimeRoute.packages.toList())

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

                    jsonSave.savePackagesInJson(bruteForceDistanceRoute.packages.toList())

                    println("Starting point: $startLocation")
                    bruteForceDistanceRoute.packages.forEachIndexed { index, pckg ->
                        println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                    }

                    println("Ending point: $lastLocation")
                    println("Total distance: ${bruteForceDistanceRoute.totalDistance} meters\n\n")

                }

                /***Nearest NEIGHBOUR***/
                if (nearestNeighbourDistanceRoute.packages.size % 5 == 0 || nearestNeighbourDistanceRoute.packages.size == 4) { //not print from 6 to 9

                    println("--------------------------------------------------")
                    println("Nearest neighbour Optimized route by TRAVEL TIME: ")
                    println("Algorithm time: $nearestNeighbourTravelTimeTime ns")
                    println("--------------------------------------------------")

                    jsonSave.savePackagesInJson(nearestNeighbourTravelTimeRoute.packages.toList())

                    println("Starting point: $startLocation")
                    nearestNeighbourTravelTimeRoute.packages.forEachIndexed { index, pckg ->
                        println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                    }
                    println("Ending point: $lastLocation")
                    println("Total travel time: ${nearestNeighbourTravelTimeRoute.totalTime} seconds\n\n")



                    println("-----------------------------------")
                    println("Nearest neighbour Optimized route by DISTANCE: ")
                    println("Algorithm time: $nearestNeighbourDistanceTime ns")
                    println("-----------------------------------")

                    jsonSave.savePackagesInJson(bruteForceDistanceRoute.packages.toList())

                    println("Starting point: $startLocation")
                    nearestNeighbourDistanceRoute.packages.forEachIndexed { index, pckg ->
                        println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                    }
                    println("Ending point: $lastLocation")
                    println("Total distance: ${nearestNeighbourDistanceRoute.totalDistance} meters\n\n")
                }
            }


        /****GOOGLE MAPS OPTIMIZATION****/

        if (callingDirectionsAPI) {

            val directionsAPITime = measureTimeMillis {
                packagesViewModel.computeOptimizedRouteDirectionsAPI(notOptimizedRoute)
            }


            packagesViewModel.observeOptimizedDirectionsAPIRoute()
                .observe(owner) { directionsRoute ->
                    optimizedDirectionsAPI = directionsRoute

                    println("-----------------------------------")
                    println("Directions API Optimized route: ")
                    println("Algorithm time: $directionsAPITime ms")
                    println("-----------------------------------")

                    jsonSave.savePackagesInJson(optimizedDirectionsAPI.packages.toList())

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