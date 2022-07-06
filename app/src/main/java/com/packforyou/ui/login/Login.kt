package com.packforyou.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.packforyou.data.models.DeliveryMan

private lateinit var deliveryMen: List<DeliveryMan>

@Composable
fun LoginScreen() {
    Box(Modifier.fillMaxSize()) {
        Text(text = "LOGIN SCREEN")
    }
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
