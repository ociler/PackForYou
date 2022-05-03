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
import com.packforyou.data.models.Leg
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
        val loginViewModel: ILoginViewModel = ViewModelProvider(this).get(LoginViewModelImpl::class.java)
        val packagesViewModel: PackagesViewModelImpl by viewModels()

        setContent {
            PackForYouTheme {
               // CasetaAtlas()
            }
        }

        val startLocation = Location().copy(address = packagesViewModel.getAddressFromLocation(GeoPoint(39.485892, -0.353794), this))
        val endLocation = Location().copy(address = packagesViewModel.getAddressFromLocation(GeoPoint(39.485892, -0.353794), this))
        val deliveryMan = DeliveryMan().copy(currentLocation = startLocation, endLocation = endLocation)

        val package1 = Package().copy(location = Location().copy(address = packagesViewModel.getAddressFromLocation(GeoPoint(39.452473, -0.358421), this)))
        val package2 = Package().copy(location = Location().copy(address = packagesViewModel.getAddressFromLocation(GeoPoint(39.471563, -0.370366), this)))
        val package3 = Package().copy(location = Location().copy(address = packagesViewModel.getAddressFromLocation(GeoPoint(39.481279, -0.370887), this)))
        val package4 = Package().copy(location = Location().copy(address = packagesViewModel.getAddressFromLocation(GeoPoint(39.522849, -0.417539), this)))
        val package5 = Package().copy(location = Location().copy(address = packagesViewModel.getAddressFromLocation(GeoPoint(39.555297,-0.527054), this)))


        val packages = listOf(package1, package2, package3, package4, package5)
        val route = Route(deliveryMan = deliveryMan, packages = packages, id = 0)

        //val gp = packagesViewModel.getLocationFromAddress("Carrer Arquitecte Arnau 30, 1, 46020, Valencia", applicationContext)

        //println(gp)

        GlobalScope.launch(Dispatchers.Main.immediate) {
            val optimizedRoute = packagesViewModel.getOptimizedRoute(route)

            println(optimizedRoute)
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