package com.packforyou.ui.login

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.*
import com.packforyou.data.models.DeliveryMan
import com.packforyou.navigation.Screen
import com.packforyou.ui.theme.Black
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

var user: FirebaseUser? = null
private lateinit var deliveryMen: List<DeliveryMan>
private lateinit var context: Context


@Composable
fun LoginScreen(navController: NavController, owner: ViewModelStoreOwner) {

    val viewModel =
        ViewModelProvider(owner)[LoginViewModelImpl::class.java]

    context = LocalContext.current

    var mailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = mailText,
            onValueChange = { mailText = it },
            label = { Text("Mail") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Black,
                unfocusedBorderColor = Black,
                textColor = Black
            ),
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "Mail"
                )
            }
        )

        OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )

        Button(
            onClick = {
                if(mailText.isBlank()){
                    Toast.makeText(context, "Your email cannot be empty", Toast.LENGTH_LONG)
                        .show()
                } else if (passwordText.isBlank()) {
                    Toast.makeText(context, "Your password cannot be empty", Toast.LENGTH_LONG)
                        .show()
                } else {
                    tryLogin(
                        mailText = mailText,
                        passwordText = passwordText,
                        navController = navController,
                        viewModel = viewModel
                    )
                }

            }
        ) {
            Text(text = "Login")
        }
    }
}

private fun tryLogin(
    mailText: String,
    passwordText: String,
    navController: NavController,
    viewModel: ILoginViewModel
) {
    val loginCallback = object : ILoginCallback {
        override fun onLoginSuccess() {
            navController.navigate(route = Screen.Home.route) {
                popUpTo(0)
            }
        }

        override fun onLoginFailure() {
            Toast.makeText(context, "Your email or password is not correct.", Toast.LENGTH_LONG)
                .show()
        }
    }

    viewModel.logIn(mailText, passwordText, loginCallback)
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
