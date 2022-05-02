package com.packforyou.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.packforyou.data.models.Client
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.repositories.IUsersRepository
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ILoginViewModel {
    fun getAllDeliveryMen()
    fun observeDeliveryMen(): LiveData<List<DeliveryMan>>
}


@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val repository: IUsersRepository
) : ILoginViewModel, ViewModel() {

    private var deliveryMen = MutableLiveData<List<DeliveryMan>>()

    override fun getAllDeliveryMen() {
        viewModelScope.launch {
            deliveryMen.postValue(repository.getAllDeliveryMen())
            println("getAllViewModel $deliveryMen")
        }
    }

    override fun observeDeliveryMen(): LiveData<List<DeliveryMan>> {
        return deliveryMen
    }


    fun addDeliveryMan(deliveryMan: DeliveryMan) {
        viewModelScope.launch {
            repository.addDeliveryMan(deliveryMan)
        }
    }

    fun addClient(client: Client){
        viewModelScope.launch {
            repository.addClient(client)
        }
    }

}
