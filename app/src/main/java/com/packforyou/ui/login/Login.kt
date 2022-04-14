package com.packforyou.ui.login

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.packforyou.data.dataSources.FirebaseDatabase
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.repositories.FirebaseCallback
import com.packforyou.data.repositories.Response

private lateinit var deliveryMen: List<DeliveryMan>

@Composable
fun Login(
    loginViewModel: LoginViewModel = viewModel()
) {

}

private fun getAllDeliveryMan(viewModel: LoginViewModel): List<DeliveryMan> {
    //FirebaseDatabase.createRandomUser()
    viewModel.getResponseUsingCallback(object : FirebaseCallback {
        override fun onResponse(response: Response) {
            deliveryMen = response.deliveryMen!!
        }
    })
    return deliveryMen
}