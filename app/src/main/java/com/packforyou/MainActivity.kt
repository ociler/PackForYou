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
import com.packforyou.ui.PackForYouTheme
import com.packforyou.ui.login.ILoginViewModel
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.packages.PackagesViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val loginViewModel: ILoginViewModel =
            ViewModelProvider(this)[LoginViewModelImpl::class.java]
        val packagesViewModel: PackagesViewModelImpl by viewModels()

        setContent {
            PackForYouTheme {
                // CasetaAtlas()
            }
        }

        val startLocation = Location().copy(
            address = packagesViewModel.getAddressFromLocation(
                GeoPoint(
                    39.485892,
                    -0.353794
                ), this
            ),
            latitude = 39.485834, longitude = -0.356361
        )

        val endLocation = Location().copy(
            address = packagesViewModel.getAddressFromLocation(
                GeoPoint(
                    39.485834,
                    -0.353794
                ), this
            ),
            latitude = 39.485834, longitude = -0.356361
        )


        val deliveryMan =
            DeliveryMan().copy(currentLocation = startLocation, endLocation = endLocation)

        val valenciaPackage1 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.452473, -0.358421),
                    this
                ), latitude = 39.452473, longitude = -0.358421
            ), numPackage = 0
        )

        val valenciaPackage2 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.471563, -0.370366),
                    this
                ), latitude = 39.471563, longitude = -0.370366
            ), numPackage = 1
        )

        val valenciaPackage3 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.481279, -0.370887),
                    this
                ), latitude = 39.481279, longitude = -0.370887
            ), numPackage = 2
        )
        val rocafortPackage = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.522849, -0.417539),
                    this
                ), latitude = 39.522849, longitude = -0.417539
            ), numPackage = 3
        )

        val entrepinsPackage1 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.555297, -0.527054),
                    this
                ), latitude = 39.555297, longitude = -0.527054
            ), numPackage = 4
        )

        val entrepinsPackage2 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.551436, -0.517451),
                    this
                ), latitude = 39.436275, longitude = -0.462388
            ), numPackage = 4
        )


        val packages = listOf(valenciaPackage2, valenciaPackage1, valenciaPackage3)
        val route = Route(deliveryMan = deliveryMan, packages = packages, id = 0)
        var optimizedBruteForceRoute: Route
        var optimizedClosestNeighbour: Route


        println("Not optimized route -> $route")

        //first we fill all the distances arrays. This function, when it finishes, will call the one to get endArray, and when it finishes the one to get the array between all packages
        //Here we need the endLocation as well because we have no other way to get this Location. Maybe it is a bit strange because of the name
        packagesViewModel.computeDistanceBetweenStartLocationAndPackages(startLocation, endLocation, packages)

        packagesViewModel.travelTimeArray
            .observe(this@MainActivity) { travelTimeArray ->

                /****BRUTE FORCE****/
                optimizedBruteForceRoute = packagesViewModel.getOptimizedRouteBruteForce(
                    route,
                    travelTimeArray
                ) //TODO maybe this should be in another thread, as it will take some time (theoretically)

                /****CLOSEST NEIGHBOUR ****/
                optimizedClosestNeighbour = packagesViewModel.getOptimizedRouteClosestNeighbour(
                    deliveryMan.currentLocation!!,
                    deliveryMan.endLocation!!,
                    route,
                    travelTimeArray,
                    packagesViewModel.startTravelTimeArray,
                    packagesViewModel.endTravelTimeArray
                )


                /***RESULTS***/

                println("------------------------------")
                println("Not Optimized route: ")
                println("------------------------------")

                println("Starting point: $startLocation")
                route.packages!!.forEachIndexed { index, pckg ->
                    println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                }
                println("Ending point: $endLocation\n\n")


                println("------------------------------")
                println("Brute Force Optimized route: ")
                println("------------------------------")

                println("Starting point: $startLocation")
                optimizedBruteForceRoute.packages!!.forEachIndexed { index, pckg ->
                    println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                }
                println("Ending point: $endLocation\n\n")



                println("-----------------------------------")
                println("Closest neighbour Optimized route: ")
                println("-----------------------------------")

                println("Starting point: $startLocation")
                optimizedClosestNeighbour.packages!!.forEachIndexed { index, pckg ->
                    println("Order: ${index + 1}, numPackage: ${pckg.numPackage},  location: ${pckg.location}")
                }
                println("Ending point: $endLocation\n\n")

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