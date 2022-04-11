package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.packforyou.data.dataSources.FirebaseDatabase
import com.packforyou.data.repositories.FirebaseCallback
import com.packforyou.data.repositories.LoginRepository
import com.packforyou.data.repositories.Response
import com.packforyou.ui.PackForYouTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: LoginRepository
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
        viewModel = ViewModelProvider(this).get(LoginRepository::class.java)
        FirebaseDatabase.createRandomUser()

        viewModel.getResponseFromFirestoreUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: Response) {
                println("inicio")
                println(response)
                println("final")
            }
        })

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