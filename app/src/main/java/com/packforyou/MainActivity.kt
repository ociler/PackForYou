package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.repositories.LoginRepositoryImpl
import com.packforyou.ui.PackForYouTheme
import com.packforyou.ui.login.LoginViewModelImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: LoginRepositoryImpl
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


        var a = LoginViewModelImpl(LoginRepositoryImpl(FirebaseRemoteDatabaseImpl()))

        GlobalScope.launch(Dispatchers.Main.immediate) {
            println(a.getAllDeliveryMen())
        }

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
/*
private fun getResponseUsingCallback() {
    loginRepository.getResponseUsingCallback(object : FirebaseCallback {
        override fun onResponse(response: Response) {
            print(response)
        }
    })
}

 */