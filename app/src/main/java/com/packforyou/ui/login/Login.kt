package com.packforyou.ui.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.*
import com.packforyou.R
import com.packforyou.data.models.*
import com.packforyou.navigation.Screen
import com.packforyou.ui.theme.*
import java.io.InputStreamReader
import java.util.*

var user: FirebaseUser? = null
private lateinit var deliveryMen: List<DeliveryMan>
private lateinit var context: Context


@Composable
fun LoginScreen(navController: NavController, owner: ViewModelStoreOwner) {

    val viewModel =
        ViewModelProvider(owner)[LoginViewModelImpl::class.java]

    //addUser(viewModel)
    context = LocalContext.current

    var mailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxHeight(.9f)
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.fillMaxSize(.12f))
                Text(
                    text = "LOGIN",
                    style = PackForYouTypography.titleLarge
                )

                Spacer(modifier = Modifier.height(30.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Pack4You logo",
                    modifier = Modifier
                        .size(75.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = mailText,
                        onValueChange = { mailText = it },
                        label = { Text(text = "EMAIL", style = PackForYouTypography.labelMedium) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Black,
                            textColor = Black
                        ),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_user),
                                contentDescription = "Email",
                                tint = Black
                            )
                        },
                        singleLine = true,
                        textStyle = PackForYouTypography.labelMedium,
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 10.dp)
                            .fillMaxWidth(.8f)
                    )


                    TextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        label = { Text("PASSWORD", style = PackForYouTypography.labelMedium) },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Black,
                            textColor = Black
                        ),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lock),
                                contentDescription = "Password",
                                tint = Black
                            )
                        },
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            val description =
                                if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, description)
                            }
                        },
                        textStyle = PackForYouTypography.labelMedium,
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 10.dp)
                            .fillMaxWidth(.8f)
                    )

                    Spacer(modifier = Modifier.height(95.dp))

                    Button(
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                        onClick = {
                            if (mailText.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Your email cannot be empty",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            } else if (passwordText.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Your password cannot be empty",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            } else {
                                tryLogin(
                                    mailText = mailText,
                                    passwordText = passwordText,
                                    navController = navController,
                                    viewModel = viewModel
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(.8f)

                    ) {
                        Text(
                            text = "Login",
                            color = White,
                            modifier = Modifier.padding(vertical = 5.dp),
                            style = PackForYouTypography.displayLarge,
                            fontSize = 20.sp
                        )
                    }
                }
            }
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
//This logic should be done on the viewModel/repository/datasource

private fun addDeliveryMan(viewModel: ILoginViewModel, deliveryMan: DeliveryMan) {
    //get reference
    val ref = FirebaseFirestore.getInstance().collection("deliveryMen")
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val deliveryManRef = ref.document(user!!.uid)

    val deliveryMan = viewModel.getExampleDeliveryMan()
    deliveryManRef.set(deliveryMan)


}

 */


/*
private fun getAllDeliveryMan(viewModel: LoginViewModelImpl): List<DeliveryMan> {
    viewModel.getResponseUsingCallback(object : FirebaseCallback {
        override fun onResponse(response: Response) {
            deliveryMen = response.deliveryMen!!
        }
    })
    return deliveryMen

 */
