package com.github.geohunt.app.ui.components.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.github.geohunt.app.LoginActivity
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.ui.components.profile.ProfileEditPage
import com.github.geohunt.app.ui.components.profile.ProfilePage
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.utility.replaceActivity

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

enum class HiddenRoutes(val route: String) {
    EditProfile("settings/profile")
}

@Composable
fun NavigationController(navController: NavHostController, modifier: Modifier = Modifier) {
    val authenticator = Authenticator.authInstance.get()
    val activity: ComponentActivity = LocalContext.current.findActivity() as ComponentActivity

    NavHost(navController, startDestination = Route.Home.route, modifier = modifier) {
        composable(Route.Home.route) {
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

        // Profile
        composable(Route.Profile.route) {
            val user = Authenticator.authInstance.get().user

            if (user == null) {
                Text("You are not logged in. Weird :(")
            } else {
                ProfilePage(
                    id = user.uid,
                    openProfileEdit = { navController.navigate(HiddenRoutes.EditProfile.route) },
                    onLogout = { logout(authenticator, activity) }
                )
            }
        }

        composable("${Route.Profile.route}/{userId}", arguments = listOf(navArgument("userId") { type = NavType.StringType })) {
            it.arguments?.getString("userId")?.let { userId -> ProfilePage(id = userId) }
        }

        composable(HiddenRoutes.EditProfile.route) {
            ProfileEditPage(
                onBackButton = { navController.popBackStack() }
            )
        }

    }
}

private fun logout(authenticator: Authenticator, activity: ComponentActivity) {
    authenticator.signOut(activity).thenAccept {
        activity.replaceActivity(Intent(activity, LoginActivity::class.java))
    }
}