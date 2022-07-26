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
    lifecycleOwner: LifecycleOwner,
    route: Route
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
            HomeScreen(navController = navController, viewModelOwner = viewModelOwner, route = route, lifecycleOwner = lifecycleOwner)
        }

        composable(
            route = Screen.StartRoute.route
        ) {
            val packages = CurrentSession.packagesToDeliver
            StartRouteScreen(packagesList = packages, navController = navController, owner = viewModelOwner)
        }
    }
}