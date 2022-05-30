package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.Observer
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
            )
        )
        val endLocation = Location().copy(
            address = packagesViewModel.getAddressFromLocation(
                GeoPoint(
                    39.485892,
                    -0.353794
                ), this
            )
        )
        val deliveryMan =
            DeliveryMan().copy(currentLocation = startLocation, endLocation = endLocation)

        val package1 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.452473, -0.358421),
                    this
                ), latitude = 39.452473, longitude = -0.358421
            ), numPackage = 0
        )
        val package2 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.471563, -0.370366),
                    this
                ), latitude = 39.471563, longitude = -0.370366
            ), numPackage = 1
        )
        val package3 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.481279, -0.370887),
                    this
                ), latitude = 39.481279, longitude = -0.370887
            ), numPackage = 2
        )
        val package4 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.522849, -0.417539),
                    this
                ), latitude = 39.522849, longitude = -0.417539
            ), numPackage = 3
        )
        val package5 = Package().copy(
            location = Location().copy(
                address = packagesViewModel.getAddressFromLocation(
                    GeoPoint(39.555297, -0.527054),
                    this
                ), latitude = 39.555297, longitude = -0.527054
            ), numPackage = 4
        )


        val packages = listOf(package2, package1, package3)
        val route = Route(deliveryMan = deliveryMan, packages = packages, id = 0)

        println("Not optimized route 1 -> $route")
        //val optimizedRouteMaps = packagesViewModel.getOptimizedRouteMaps(route)
        println("whatever")

        //first we fill the distances array
        packagesViewModel.computeDistanceBetweenAllPackages(route.packages!!)

        packagesViewModel.observeTravelTimeArray()
            .observe(this@MainActivity) { optimizedArray ->
                val optimizedRoute = packagesViewModel.getOptimizedRouteBruteForce(
                    route,
                    optimizedArray
                ) //TODO maybe this should be in another thread, as it will take some time (theoretically)

                println("Not optimized route 2 -> $route")
                println("Optimized route -> $optimizedRoute")

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