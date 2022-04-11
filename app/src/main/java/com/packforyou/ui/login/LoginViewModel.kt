package com.packforyou.ui.login

import androidx.lifecycle.ViewModel
import com.packforyou.data.repositories.FirebaseCallback
import com.packforyou.data.repositories.LoginRepository

class LoginViewModel(
    private val repository: LoginRepository = LoginRepository()
) : ViewModel() {
    fun getResponseUsingCallback(callback: FirebaseCallback) =
        repository.getResponseFromFirestoreUsingCallback(callback)
}