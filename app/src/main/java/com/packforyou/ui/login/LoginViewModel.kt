package com.packforyou.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.repositories.ILoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import javax.inject.Inject

interface ILoginViewModel {

}

@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val repository: ILoginRepository
) : ILoginViewModel, ViewModel() {
    var deliveryMen = listOf<DeliveryMan>()

    fun getAllDeliveryMen(): List<DeliveryMan> {
        viewModelScope.launch {
            deliveryMen = repository.getAllDeliveryMen()
        }
        return deliveryMen
    }


    fun addDeliveryMan(deliveryMan: DeliveryMan) {
        viewModelScope.launch {
            repository.addDeliveryMan(deliveryMan)
        }
    }

}
