package com.packforyou.ui.login

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.packforyou.api.ICallbackAPICalls
import com.packforyou.data.models.Client
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.models.Package
import com.packforyou.data.repositories.IUsersRepository
import com.packforyou.navigation.Screen
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ILoginViewModel {
    fun getAllDeliveryMen()
    fun observeDeliveryMen(): LiveData<List<DeliveryMan>>
    fun addDeliveryMan(deliveryMan: DeliveryMan)
    fun addClient(client: Client)
    fun logOut()
    fun logIn(mail: String, password: String, callbackObject: ILoginCallback)
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


    override fun addDeliveryMan(deliveryMan: DeliveryMan) {
        viewModelScope.launch {
            repository.addDeliveryMan(deliveryMan)
        }
    }

    override fun addClient(client: Client){
        viewModelScope.launch {
            repository.addClient(client)
        }
    }

    override fun logOut() {
        CurrentSession.packagesToDeliver = mutableStateOf(listOf())
        CurrentSession.packagesForToday = mutableStateOf(listOf())
        CurrentSession.firstAccess = true
        CurrentSession.lastLocationsList.value = listOf()
        CurrentSession.deliveryMan = null
    }

    override fun logIn(mail: String, password: String, callbackObject: ILoginCallback) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(mail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Successfully signed in
                    callbackObject.onLoginSuccess()

                } else {
                    callbackObject.onLoginFailure()
                }

            }
    }

}
