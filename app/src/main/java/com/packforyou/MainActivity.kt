package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.GeoPoint
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import com.packforyou.data.repositories.IPackagesAndAtlasRepository
import com.packforyou.ui.PackForYouTheme
import com.packforyou.ui.atlas.Atlas
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.login.ILoginViewModel
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.atlas.AtlasWithGivenRoute
import com.packforyou.ui.atlas.IAtlasViewModel
import com.packforyou.ui.packages.IPackagesViewModel
import com.packforyou.ui.packages.PackagesViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.measureNanoTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val loginViewModel: ILoginViewModel by viewModels()
            //= ViewModelProvider(this)[LoginViewModelImpl::class.java]
        val packagesViewModel: PackagesViewModelImpl by viewModels() //TODO dir-li a David qupe fer ací

        val atlasViewModel: AtlasViewModelImpl by viewModels()

        val startLocation = Location().copy(
            address = packagesViewModel.getAddressFromLocation(
                GeoPoint(
                    39.509074,
                    -0.409842
                ), this
            ),
            latitude = 39.509074, longitude = -0.409842
        )

        val endLocation = Location().copy(
            address = packagesViewModel.getAddressFromLocation(
                GeoPoint(
                    39.429299,
                    -0.363321
                ), this
            ),
            latitude = 39.429299, longitude = -0.363321
        )


        val deliveryMan =
            DeliveryMan().copy(currentLocation = startLocation, endLocation = endLocation)

        val valenciaPackage1 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.452473, -0.358421),
                    this
                ), latitude = 39.452473, longitude = -0.358421,
                city = "Valencia"
            ), numPackage = 0
        )

        val valenciaPackage2 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.471563, -0.370366),
                    this
                ), latitude = 39.471563, longitude = -0.370366,
                city = "Valencia"
            ), numPackage = 1
        )

        val valenciaPackage3 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.481279, -0.370887),
                    this
                ), latitude = 39.481279, longitude = -0.370887,
                city = "Valencia"
            ), numPackage = 2
        )
        val rocafortPackage = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.522849, -0.417539),
                    this
                ), latitude = 39.522849, longitude = -0.417539,
                city = "Rocafort"
            ), numPackage = 3
        )

        val entrepinsPackage1 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.555297, -0.527054),
                    this
                ), latitude = 39.555297, longitude = -0.527054,
                city = "Entrepins"
            ), numPackage = 4
        )

        val entrepinsPackage2 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.551436, -0.517451),
                    this
                ), latitude = 39.436275, longitude = -0.462388,
                city = "Entrepins"
            ), numPackage = 4
        )


        val packages = listOf(valenciaPackage2, valenciaPackage1, valenciaPackage3, rocafortPackage)
        val notOptimizedRoute =
            Route(deliveryMan = deliveryMan, packages = packages, id = 0, totalTime = 0)

        var bruteForceTravelTimeRoute: Route
        var closestNeighbourTravelTimeRoute: Route

        var bruteForceDistanceRoute: Route
        var closestNeighbourDistanceRoute: Route

        var optimizedDirectionsAPI: Route


        //first we fill all the distances arrays. This function, when it finishes, will call the one to get endArray, and when it finishes the one to get the array between all packages
        //Here we need the endLocation as well because we have no other way to get this Location. Maybe it is a bit strange because of the name
        packagesViewModel.computeDistanceBetweenStartLocationAndPackages(
            startLocation,
            endLocation,
            packages
        )

        packagesViewModel.observeTravelTimeArray()
            .observe(this) { travelTimeArray ->

                /****NOT OPTIMIZED****/
                notOptimizedRoute.totalTime = packagesViewModel.getRouteTravelTime(
                    deliveryMan.currentLocation!!,
                    deliveryMan.endLocation!!,
                    notOptimizedRoute.packages!!,
                    travelTimeArray,
                    packagesViewModel.getStartTravelTimeArray(),
                    packagesViewModel.getEndTravelTimeArray()
                )

                /****BRUTE FORCE****/
                val bruteForceTravelTimeTime = measureNanoTime {
                    bruteForceTravelTimeRoute =
                        packagesViewModel.getOptimizedRouteBruteForceTravelTime(
                            notOptimizedRoute,
                            travelTimeArray
                        ) //TODO maybe this should be in another thread, as it will take some time (theoretically)
                }

                val bruteForceDistanceTime = measureNanoTime {
                    bruteForceDistanceRoute = packagesViewModel.getOptimizedRouteBruteForceDistance(
                        notOptimizedRoute,
                        packagesViewModel.getDistanceArray()
                    ) //TODO maybe this should be in another thread, as it will take some time (theoretically)
                }

                /****CLOSEST NEIGHBOUR ****/
                val closestNeighbourTravelTimeTime = measureNanoTime {
                    closestNeighbourTravelTimeRoute =
                        packagesViewModel.getOptimizedRouteClosestNeighbourDistance(
                            deliveryMan.currentLocation!!,
                            deliveryMan.endLocation!!,
                            notOptimizedRoute,
                            travelTimeArray,
                            packagesViewModel.getStartDistanceArray(),
                            packagesViewModel.getEndDistanceArray()
                        )
                }

                val closestNeighbourDistanceTime = measureNanoTime {
                    closestNeighbourDistanceRoute =
                        packagesViewModel.getOptimizedRouteClosestNeighbourDistance(
                            deliveryMan.currentLocation!!,
                            deliveryMan.endLocation!!,
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
                println("Total travel time: ${notOptimizedRoute.totalTime} seconds\n\n")


                /***BRUTE FORCE***/
                println("--------------------------------------------")
                println("Brute Force Optimized route by TRAVEL TIME: ")
                println("Algorithm time: $bruteForceTravelTimeTime ns")
                println("--------------------------------------------")

                println("Starting point: $startLocation")
                bruteForceTravelTimeRoute.packages!!.forEachIndexed { index, pckg ->
                    println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                }

                println("Ending point: $endLocation")
                println("Total travel time: ${bruteForceTravelTimeRoute.totalTime} seconds\n\n")


                println("-----------------------------------------")
                println("Brute Force Optimized route by DISTANCE: ")
                println("Algorithm time: $bruteForceDistanceTime ns")
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

        val directionsAPITime = measureNanoTime {
            packagesViewModel.computeOptimizedRouteDirectionsAPI(notOptimizedRoute)
        }

        packagesViewModel.observeOptimizedDirectionsAPIRoute().observe(this) { directionsRoute ->
            optimizedDirectionsAPI = directionsRoute

            println("-----------------------------------")
            println("Directions API Optimized route: ")
            println("Algorithm time: $directionsAPITime ns")
            println("-----------------------------------")

            println("Starting point: $startLocation")
            optimizedDirectionsAPI.packages!!.forEachIndexed { index, pckg ->
                println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
            }

            println("Ending point: $endLocation")
            println("Total travel time: ${directionsRoute.totalTime} seconds\n\n")
        }

        val locations = arrayListOf(startLocation)

        packages.forEach {
            locations.add(it.location!!)
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