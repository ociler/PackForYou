package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.packforyou.ui.PackForYouTheme
import com.packforyou.ui.login.Login
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.map.Atlas
import com.packforyou.ui.map.CasetaAtlas
import com.packforyou.ui.packages.PackagesViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val loginViewModel: LoginViewModelImpl by viewModels() //TODO no deuria ser ILoginViewModel? Hilt no em deixa fer això. Pensava que per a esto estava el Provide
        super.onCreate(savedInstanceState)
        setContent {
            PackForYouTheme {
                CasetaAtlas()
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