package com.github.geohunt.app.ui.components.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material.icons.sharp.Search
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.geohunt.app.R
import androidx.compose.ui.Modifier
import com.github.geohunt.app.maps.GoogleMapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

typealias ComposableFun = @Composable () -> Unit

enum class Route(val titleStringId: Int, val route: String, val icon: ComposableFun) {

    Home(R.string.navigation_home, "home", { Icon(Icons.Sharp.Home, null) }),
    Explore(R.string.navigation_explore, "explore", { Icon(Icons.Sharp.Search, null) }),
    Create(R.string.navigation_create, "create", { Icon(Icons.Sharp.Add, null) }),
    ActiveHunts(R.string.navigation_active_hunts, "active-hunts", { Icon(androidx.compose.ui.res.painterResource(
        id = R.drawable.target_arrow
    ), null) }),
    Profile(R.string.navigation_profile, "profile", { Icon(Icons.Sharp.Person, null) })

}

@Composable
fun NavigationController(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Route.Home.route, modifier = modifier) {
        composable(Route.Home.route) {
            Text("Home")
        }
        composable(Route.Explore.route) {
            val epflCoordinates = LatLng(46.519585, 6.5684919)
            val epflCameraPositionState = CameraPositionState(CameraPosition(epflCoordinates, 15f, 0f, 0f))
            GoogleMapView(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = epflCameraPositionState

            )
        }
        composable(Route.Create.route) {
            Text("Create")
        }
        composable(Route.ActiveHunts.route) {
            Text("Active hunts")
        }
        composable(Route.Profile.route) {
            Text("Profile")
        }

    }
}