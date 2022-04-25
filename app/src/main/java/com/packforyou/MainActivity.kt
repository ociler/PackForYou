package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.Observer
import com.packforyou.data.ExampleObjects
import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.repositories.UsersRepositoryImpl
import com.packforyou.data.repositories.PackagesRepositoryImpl
import com.packforyou.ui.PackForYouTheme
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.packages.PackagesViewModelImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: UsersRepositoryImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PackForYouTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }


        var a = LoginViewModelImpl(UsersRepositoryImpl(FirebaseRemoteDatabaseImpl()))
        var b = PackagesViewModelImpl(PackagesRepositoryImpl(FirebaseRemoteDatabaseImpl()))


        GlobalScope.launch(Dispatchers.Main.immediate) {
           // a.addDeliveryMan(DeliveryMan())
            //b.addPackage(Package().copy(numPackage = 1))

            ExampleObjects().addExamplePackage()
            ExampleObjects().addExampleDeliveryMan()
            ExampleObjects().addExampleClient()



        }





        println("PreGet")
        a.getAllDeliveryMen()
        a.observeDeliveryMen().observe(this/*lifecycelOwner*/, deliveryMenObserver)
        println("PostGet")


    }
}
val deliveryMenObserver = Observer<List<DeliveryMan>>{ deliveryMen ->
    println("Acabamos de pasar $deliveryMen")
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
/*
private fun getResponseUsingCallback() {
    loginRepository.getResponseUsingCallback(object : FirebaseCallback {
        override fun onResponse(response: Response) {
            print(response)
        }
    })
}

 */