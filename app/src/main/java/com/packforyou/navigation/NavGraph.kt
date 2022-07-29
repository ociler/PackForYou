package com.packforyou.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.packforyou.data.models.Route
import com.packforyou.ui.home.HomeScreen
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.login.LoginScreen
import com.packforyou.ui.packages.StartRouteScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    viewModelOwner: ViewModelStoreOwner,
    lifecycleOwner: LifecycleOwner
){
    NavHost(
        navController =  navController,
        startDestination = Screen.Home.route
    ) {

        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(navController = navController, owner = viewModelOwner)
        }

        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(navController = navController, viewModelOwner = viewModelOwner, lifecycleOwner = lifecycleOwner)
        }

        composable(
            route = Screen.StartRoute.route
        ) {
            StartRouteScreen(navController = navController, owner = viewModelOwner)
        }
    }
}