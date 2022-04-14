package com.packforyou.ui.login

import androidx.lifecycle.ViewModel
import com.packforyou.data.repositories.FirebaseCallback
import com.packforyou.data.repositories.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository = LoginRepository()
) : ViewModel() {
    fun getResponseUsingCallback(callback: FirebaseCallback) =
        repository.getResponseFromFirestoreUsingCallback(callback)
}