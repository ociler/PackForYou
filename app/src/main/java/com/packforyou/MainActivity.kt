package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.packforyou.data.models.Location
import com.packforyou.data.models.Step
import com.packforyou.ui.PackForYouTheme
import com.packforyou.ui.login.ILoginViewModel
import com.packforyou.ui.login.Login
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.map.Atlas
import com.packforyou.ui.map.CasetaAtlas
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

        setContent {
            PackForYouTheme {
               // CasetaAtlas()
            }
        }

        val packagesViewModel: PackagesViewModelImpl by viewModels()

        val gp = packagesViewModel.getLocationFromAddress("Carrer Arquitecte Arnau 30, 1, 46020, Valencia", applicationContext)

        println(gp)
        var whatever = Step()

        GlobalScope.launch(Dispatchers.Main.immediate) {
            whatever = packagesViewModel.getStep(
                Location().copy(latitude = 38.807461, longitude = -0.881730),
                Location().copy(latitude = 39.486408, longitude = -0.353470)
            )

            println(whatever)
        }






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