package org.sergey.forecast.presentation.ui

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.sergey.forecast.presentation.ui.screen.ForecastScreen
import org.sergey.forecast.presentation.ui.screen.NearbyStationsScreen
import org.sergey.forecast.presentation.ui.screen.SetLocationScreen
import org.sergey.forecast.presentation.viewmodel.ForecastViewModel
import org.sergey.forecast.presentation.viewmodel.NearbyStationsViewModel

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.SetLocation.route) {
        composable(Screen.SetLocation.route) {
            SetLocationScreen(
                navToNearbyStations = { lat, lon, radius ->
                    navController.navigate(Screen.NearbyStations.createRoute(lat, lon, radius))
                }
            )
        }
        composable(
            route = Screen.NearbyStations.route,
            arguments = listOf(
                navArgument(NavArgs.NearbyStations.LATITUDE) { type = NavType.StringType },
                navArgument(NavArgs.NearbyStations.LONGITUDE) { type = NavType.StringType },
                navArgument(NavArgs.NearbyStations.RADIUS_METERS) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString(NavArgs.NearbyStations.LATITUDE)
                ?.toDoubleOrNull() ?: 0.0
            val lon = backStackEntry.arguments?.getString(NavArgs.NearbyStations.LONGITUDE)
                ?.toDoubleOrNull() ?: 0.0
            val radiusMeters =
                backStackEntry.arguments?.getInt(NavArgs.NearbyStations.RADIUS_METERS) ?: 100000
            val viewModel: NearbyStationsViewModel = hiltViewModel()
            viewModel.setupCoordinates(lat, lon, radiusMeters)
            NearbyStationsScreen(
                viewModel = viewModel, navToSetLocation = {
                    navController.navigate(Screen.SetLocation.route)
                },
                navToForecast = { id ->
                    navController.navigate(Screen.Forecast.createRoute(id)) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = Screen.Forecast.route,
            arguments = listOf(
                navArgument(NavArgs.Forecast.STATION_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString(NavArgs.Forecast.STATION_ID) ?: ""
            val viewModel: ForecastViewModel = hiltViewModel()
            viewModel.setStationId(stationId)
            ForecastScreen(
                viewModel = viewModel,
                navToSetLocation = {
                    navController.navigate(Screen.SetLocation.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object SetLocation : Screen("set_location")
    object NearbyStations : Screen(NavArgs.NearbyStations.ROUTE_PATTERN) {
        fun createRoute(lat: Double, lon: Double, radiusMeters: Int) =
            "nearby_stations/$lat/$lon/$radiusMeters"
    }
    object Forecast : Screen(NavArgs.Forecast.ROUTE_PATTERN) {
        fun createRoute(stationId: String) =
            "forecast/$stationId"
    }
}

object NavArgs {
    object NearbyStations {
        const val LATITUDE = "lat"
        const val LONGITUDE = "lon"
        const val RADIUS_METERS = "radius_meters"
        const val ROUTE_PATTERN = "nearby_stations/{$LATITUDE}/{$LONGITUDE}/{$RADIUS_METERS}"
    }

    object Forecast {
        const val STATION_ID = "station_id"
        const val ROUTE_PATTERN = "forecast/{$STATION_ID}"
    }
}