package com.packforyou.ui.login

import androidx.compose.runtime.Composable
import com.packforyou.data.models.DeliveryMan

private lateinit var deliveryMen: List<DeliveryMan>

@Composable
fun Login(
    loginViewModel: ILoginViewModel
) {

}
/*
private fun getAllDeliveryMan(viewModel: LoginViewModelImpl): List<DeliveryMan> {
    viewModel.getResponseUsingCallback(object : FirebaseCallback {
        override fun onResponse(response: Response) {
            deliveryMen = response.deliveryMen!!
        }
    })
    return deliveryMen

 */
