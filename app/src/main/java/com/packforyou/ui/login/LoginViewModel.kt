package com.packforyou.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.repositories.ILoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ILoginViewModel {

}
@HiltViewModel

class LoginViewModelImpl @Inject constructor(
    private val repository: ILoginRepository
) : ILoginViewModel, ViewModel() {
    var deliveryMen = listOf<DeliveryMan>()

    suspend fun getAllDeliveryMen(): List<DeliveryMan> {
        deliveryMen = repository.getAllDeliveryMen()
        return repository.getAllDeliveryMen()
    }



    suspend fun addDeliveryMan() = repository.addDeliveryMan(DeliveryMan())

}
