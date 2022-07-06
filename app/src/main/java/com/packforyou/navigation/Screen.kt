package com.packforyou.navigation

sealed class Screen(val route: String) {
    object Login: Screen(route = "login_screen")
    object Home: Screen(route = "home_screen")
    object StartRoute : Screen("start_route_screen")
}
