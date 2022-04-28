package com.packforyou

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.GeoPoint
import com.packforyou.data.ExampleObjects
import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.repositories.UsersRepositoryImpl
import com.packforyou.data.repositories.PackagesRepositoryImpl
import com.packforyou.ui.PackForYouTheme
import com.packforyou.ui.login.ILoginViewModel
import com.packforyou.ui.login.Login
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.packages.PackagesViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val loginViewModel: LoginViewModelImpl by viewModels() //TODO no deuria ser ILoginViewModel? Hilt no em deixa fer això. Pensava que per a esto estava el Provide
        super.onCreate(savedInstanceState)
        setContent {
            PackForYouTheme {
                //Login(loginViewModel)
            }
        }

        val packagesViewModel: PackagesViewModelImpl by viewModels()

        val gp = packagesViewModel.getLocationFromAddress("Carrer Arquitecte Arnau 30, 1, 46020, Valencia", applicationContext)

        println(gp)
/*
        GlobalScope.launch(Dispatchers.Main.immediate) {
            // a.addDeliveryMan(DeliveryMan())
            //b.addPackage(Package().copy(numPackage = 1))

            ExampleObjects().addExamplePackage()
            ExampleObjects().addExampleDeliveryMan()
            ExampleObjects().addExampleClient()
        }

 */




        println("PreGet")
        loginViewModel.getAllDeliveryMen()
        loginViewModel.observeDeliveryMen().observe(this/*lifecycelOwner*/
        ) { deliveryMen ->
            println("Acabamos de pasar $deliveryMen")
        }
        println("PostGet")


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