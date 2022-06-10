package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import com.packforyou.ui.PackForYouTheme
import com.packforyou.ui.atlas.Atlas
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.atlas.IAtlasViewModel
import com.packforyou.ui.login.ILoginViewModel
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.packages.IPackagesViewModel
import com.packforyou.ui.packages.PackagesViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStreamReader
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var loginViewModel: ILoginViewModel
    lateinit var packagesViewModel: IPackagesViewModel
    lateinit var atlasViewModel: IAtlasViewModel


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this)[LoginViewModelImpl::class.java]

        packagesViewModel =
            ViewModelProvider(this)[PackagesViewModelImpl::class.java]

        atlasViewModel =
            ViewModelProvider(this)[AtlasViewModelImpl::class.java]


        val mode = "Town10" //with this, we will control which situation we want to test

        //we have some json files with the locations and packages decoded. We will use them
        val startLocation = restoreStartLocation(mode)
        val endLocation = restoreEndLocation(mode)

        val packages = restorePackagesFromJson(mode).toList()

        packages.forEachIndexed { i, it ->
            it.numPackage = i
        }


        val deliveryMan =
            DeliveryMan().copy(currentLocation = startLocation, endLocation = endLocation)

        val notOptimizedRoute =
            Route(deliveryMan = deliveryMan, packages = packages, id = 0, totalTime = 0)


        var bruteForceTravelTimeRoute: Route
        var closestNeighbourTravelTimeRoute: Route

        var bruteForceDistanceRoute: Route
        var closestNeighbourDistanceRoute: Route

        var optimizedDirectionsAPI: Route


        //We have already saved our arrays from a previous run of the app. In order to make less calls,
        // we will use them instead of making calls everytime we execute the app
        /***USING LOCAL FILES****/
        restoreArraysFromJson(mode)


        //first we fill all the distances arrays. This function, when it finishes, will call the one to get endArray, and when it finishes the one to get the array between all packages
        //Here we need the endLocation as well because we have no other way to get this Location. Maybe it is a bit strange because of the name
        /*****CALLING API*****/


        /*
        packagesViewModel.computeDistanceBetweenStartLocationAndPackages(
            startLocation,
            endLocation,
            packages
        )

         */






        packagesViewModel.observeTravelTimeArray()
            .observe(this) { travelTimeArray ->

                /****NOT OPTIMIZED****/
                notOptimizedRoute.totalTime = packagesViewModel.getRouteTravelTime(
                    notOptimizedRoute.packages!!,
                    travelTimeArray,
                    packagesViewModel.getStartTravelTimeArray(),
                    packagesViewModel.getEndTravelTimeArray()
                )

                notOptimizedRoute.totalDistance = packagesViewModel.getRouteDistance(
                    notOptimizedRoute.packages!!,
                    packagesViewModel.getDistanceArray(),
                    packagesViewModel.getStartDistanceArray(),
                    packagesViewModel.getEndDistanceArray()
                )

                /****BRUTE FORCE****/

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
                    bruteForceDistanceRoute = packagesViewModel.getOptimizedRouteBruteForceDistance(
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
                notOptimizedRoute.packages!!.forEachIndexed { index, pckg ->
                    println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                }

                println("Ending point: $endLocation")
                println("Total travel time: ${notOptimizedRoute.totalTime} seconds")
                println("Total distance: ${notOptimizedRoute.totalDistance} meters\n\n")


                /***BRUTE FORCE***/
                println("--------------------------------------------")
                println("Brute Force Optimized route by TRAVEL TIME: ")
                println("Algorithm time: $bruteForceTravelTimeTime ms")
                println("--------------------------------------------")

                println("Starting point: $startLocation")
                bruteForceTravelTimeRoute.packages!!.forEachIndexed { index, pckg ->
                    println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                }

                println("Ending point: $endLocation")
                println("Total travel time: ${bruteForceTravelTimeRoute.totalTime} seconds\n\n")


                println("-----------------------------------------")
                println("Brute Force Optimized route by DISTANCE: ")
                println("Algorithm time: $bruteForceDistanceTime ms")
                println("-----------------------------------------")

                println("Starting point: $startLocation")
                bruteForceDistanceRoute.packages!!.forEachIndexed { index, pckg ->
                    println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                }

                println("Ending point: $endLocation")
                println("Total distance: ${bruteForceDistanceRoute.totalDistance} meters\n\n")


                /***CLOSEST NEIGHBOUR***/
                println("--------------------------------------------------")
                println("Closest neighbour Optimized route by TRAVEL TIME: ")
                println("Algorithm time: $closestNeighbourTravelTimeTime ns")
                println("--------------------------------------------------")

                println("Starting point: $startLocation")
                closestNeighbourTravelTimeRoute.packages!!.forEachIndexed { index, pckg ->
                    println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                }
                println("Ending point: $endLocation")
                println("Total travel time: ${closestNeighbourTravelTimeRoute.totalTime} seconds\n\n")



                println("-----------------------------------")
                println("Closest neighbour Optimized route by DISTANCE: ")
                println("Algorithm time: $closestNeighbourDistanceTime ns")
                println("-----------------------------------")

                println("Starting point: $startLocation")
                closestNeighbourDistanceRoute.packages!!.forEachIndexed { index, pckg ->
                    println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                }
                println("Ending point: $endLocation")
                println("Total distance: ${closestNeighbourDistanceRoute.totalDistance} meters\n\n")
            }


        /****GOOGLE MAPS OPTIMIZATION****/

        val directionsAPITime = measureTimeMillis {
            packagesViewModel.computeOptimizedRouteDirectionsAPI(notOptimizedRoute)
        }

        packagesViewModel.observeOptimizedDirectionsAPIRoute().observe(this) { directionsRoute ->
            optimizedDirectionsAPI = directionsRoute

            println("-----------------------------------")
            println("Directions API Optimized route: ")
            println("Algorithm time: $directionsAPITime ms")
            println("-----------------------------------")

            println("Starting point: $startLocation")
            optimizedDirectionsAPI.packages!!.forEachIndexed { index, pckg ->
                println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
            }

            println("Ending point: $endLocation")
            println("Total travel time: ${directionsRoute.totalTime} seconds")
            println("Total distance: ${directionsRoute.totalDistance} meters\n\n")
        }

        val locations = arrayListOf(startLocation)

        packages.forEach {
            locations.add(it.location)
        }
        locations.add(endLocation)

        setContent {
            PackForYouTheme {
                Atlas(atlasViewModel, notOptimizedRoute)
            }
        }

    }


/*

        println("PreGet")
        loginViewModel.getAllDeliveryMen()
        loginViewModel.observeDeliveryMen().observe(this/*lifecycelOwner*/
        ) { deliveryMen ->
            println("Acabamos de pasar $deliveryMen")
        }
        println("PostGet")

 */

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
        packages.forEach {
            println("PACKAGE ${it.numPackage}")
            println("--------------------")
            println(Gson().toJson(it))
            println("--------------------\n\n")
        }
    }

    private fun restoreArraysFromJson(mode: String) {
        val gson = Gson()
        var reader: InputStreamReader

        when (mode) {

            "City10" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_valencia10))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_valencia10))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader = InputStreamReader(resources.openRawResource(R.raw.end_distance_valencia10))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_valencia10))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_valencia10))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_valencia10))
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
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_town10))
                packagesViewModel.setStartDistanceArray(gson.fromJson(reader, IntArray::class.java))

                reader = InputStreamReader(resources.openRawResource(R.raw.end_distance_town10))
                packagesViewModel.setEndDistanceArray(gson.fromJson(reader, IntArray::class.java))


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_town10))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_town10))
                packagesViewModel.setEndTravelTimeArray(gson.fromJson(reader, IntArray::class.java))

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town10))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            else -> println("Sorry, not valid mode given")
        }
    }


    private fun restorePackagesFromJsonManually(mode: String): ArrayList<Package> {
        val packages = arrayListOf<Package>()
        val gson = Gson()
        var reader: InputStreamReader

        when (mode) {
            "City10" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.benimaclet1))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.benimaclet2))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.benimaclet3))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.benimaclet4))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.benimaclet5))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.benimaclet6))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.benimaclet7))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.rascanya1))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.rascanya2))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.rascanya3))
            }

            "Town10" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.moixent1))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.moixent4))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.la_font1))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.vallada1))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.moixent2))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.montesa2))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.la_font2))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.moixent3))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.montesa1))
                packages.add(gson.fromJson(reader, Package::class.java))
                reader = InputStreamReader(resources.openRawResource(R.raw.moixent5))
                packages.add(gson.fromJson(reader, Package::class.java))
            }

            else -> {
                println("Sorry, no valid mode given")
            }
        }

        return packages
    }

    private fun restorePackagesFromJson(mode: String): Array<Package> {
        var packages = arrayOf<Package>()
        val gson = Gson()
        val reader: InputStreamReader

        when (mode) {
            "City10" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town10" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages10))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town11" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages11))
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
            "City10" -> {
                val reader =
                    InputStreamReader(resources.openRawResource(R.raw.valencia_start_location))
                startLocation = Gson().fromJson(reader, Location::class.java)
            }

            "Town10" -> {
                val reader = InputStreamReader(resources.openRawResource(R.raw.town_start_location))
                return Gson().fromJson(reader, Location::class.java)
            }

            else -> {
                println("Sorry, no valid mode given")
            }
        }
        return startLocation
    }

    private fun restoreEndLocation(mode: String): Location {
        var endLocation = Location()
        when (mode) {
            "City10" -> {
                val reader =
                    InputStreamReader(resources.openRawResource(R.raw.valencia_end_location))
                endLocation = Gson().fromJson(reader, Location::class.java)
            }

            "Town10" -> {
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


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PackForYouTheme {
        Greeting("Android")
    }
}
