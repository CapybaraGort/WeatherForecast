package org.sergey.forecast.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.sergey.forecast.presentation.ui.screen.SetLocationScreen

@Composable
fun MainNavHost() {

    val navController = rememberNavController()

    NavHost(navController, Screen.SetLocation.route) {
        composable(Screen.SetLocation.route) {
            SetLocationScreen()
        }
    }
}

sealed class Screen(val route: String) {
    object SetLocation : Screen("set_location")
}