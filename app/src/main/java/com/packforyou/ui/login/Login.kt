package com.packforyou.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.lifecycle.ViewModelProvider
import com.packforyou.data.dataSources.FirebaseDatabase
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.repositories.FirebaseCallback
import com.packforyou.data.repositories.Response


private lateinit var viewModel: LoginViewModel
private lateinit var deliveryMen: List<DeliveryMan>

@Composable
fun Login() {
    //viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

}

private fun getAllDeliveryMan(): List<DeliveryMan> {
    FirebaseDatabase.createRandomUser()

    viewModel.getResponseUsingCallback(object : FirebaseCallback {
        override fun onResponse(response: Response) {
            deliveryMen = response.deliveryMen!!
        }
    })
    return deliveryMen
}