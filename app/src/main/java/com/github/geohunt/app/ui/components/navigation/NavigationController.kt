package com.github.geohunt.app.ui.components.navigation

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material.icons.sharp.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.geohunt.app.R
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.maps.GoogleMapDisplay
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.components.ClaimChallenge
import com.github.geohunt.app.ui.components.challengecreation.CreateNewChallenge
import com.github.geohunt.app.ui.components.ZoomableImageView
import com.github.geohunt.app.ui.components.activehunts.ActiveHunts
import com.github.geohunt.app.ui.components.challenge.ChallengeView
import com.github.geohunt.app.ui.components.profile.ProfilePage
import com.github.geohunt.app.utility.findActivity
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.github.geohunt.app.LoginActivity
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.ui.components.profile.edit.ProfileEditPage
import com.github.geohunt.app.ui.settings.app_settings.AppSettingsPage
import com.github.geohunt.app.ui.settings.app_settings.AppSettingsViewModel
import com.github.geohunt.app.ui.settings.SettingsPage
import com.github.geohunt.app.ui.settings.privacysettings.PrivacySettingsPage
import com.github.geohunt.app.ui.settings.privacysettings.PrivacySettingsViewModel
import com.github.geohunt.app.utility.replaceActivity

typealias ComposableFun = @Composable () -> Unit

interface Route {
    val route: String
}

enum class VisibleRoute(val titleStringId: Int, override val route: String, val icon: ComposableFun): Route {

    Home(R.string.navigation_home, "home", { Icon(Icons.Sharp.Home, null) }),
    Explore(R.string.navigation_explore, "explore", { Icon(Icons.Sharp.Search, null) }),
    Create(R.string.navigation_create, "create", { Icon(Icons.Sharp.Add, null) }),
    ActiveHunts(R.string.navigation_active_hunts, "active-hunts", {
        Icon(
            androidx.compose.ui.res.painterResource(
                id = R.drawable.target_arrow
            ), null
        )
    }),
    Profile(R.string.navigation_profile, "profile", { Icon(Icons.Sharp.Person, null) })
}

enum class HiddenRoute(override val route: String): Route {
    EditProfile("settings/profile"),
    Settings("settings"),
    AppSettings("settings/app"),
    PrivacySettings("settings/privacy"),
    Leaderboard("leaderboard")
}

@Composable
fun NavigationController(
    navController: NavHostController,
    database: Database,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authenticator = Authenticator.authInstance.get()
    val activity: ComponentActivity = LocalContext.current.findActivity() as ComponentActivity
    val container: AppContainer = AppContainer.get(LocalContext.current.applicationContext as Application)

    NavHost(navController, startDestination = VisibleRoute.Home.route, modifier = modifier) {
        composable(VisibleRoute.Home.route) {
        }
        composable(VisibleRoute.Explore.route) {
            val epflCoordinates = LatLng(46.519585, 6.5684919)
            val epflCameraPosition = CameraPosition(epflCoordinates, 15f, 0f, 0f)
            GoogleMapDisplay(
                modifier = Modifier.fillMaxSize(),
                cameraPosition = epflCameraPosition
            )
        }
        composable(VisibleRoute.Create.route) {
            CreateNewChallenge(
                database = database,
                onChallengeCreated = { challenge ->
                    navController.popBackStack()
                    navController.navigate("challenge-view/${challenge.cid}")
                },
                onFailure = {
                    Toast.makeText(context, "Something went wrong, failed to create challenge", Toast.LENGTH_LONG).show()
                    Log.e("GeoHunt", "Fail to create challenge: $it")
                    navController.popBackStack()
                }
            )
        }

        composable(VisibleRoute.ActiveHunts.route) {
            val user = Authenticator.authInstance.get().user

            if (user == null) {
                Text("You are not logged in. Weird :(")
            } else {
                ActiveHunts(id = user.uid, database) {
                    navController.navigate(VisibleRoute.Explore.route)
                }
            }
        }

        // Profile
        composable(VisibleRoute.Profile.route) {
            val user = Authenticator.authInstance.get().user

            if (user == null) {
                Text("You are not logged in. Weird :(")
            } else {
                ProfilePage(
                    id = user.uid,
                    openProfileEdit = { navController.navigate(HiddenRoute.EditProfile.route) },
                    onLogout = { logout(authenticator, activity) },
                    openSettings = { navController.navigate(HiddenRoute.Settings.route) },
                    database = database
                )
            }
        }

        composable("${VisibleRoute.Profile.route}/{userId}", arguments = listOf(navArgument("userId") { type = NavType.StringType })) {
            it.arguments?.getString("userId")?.let { userId -> ProfilePage(id = userId, database = database) }
        }

        composable(HiddenRoute.EditProfile.route) {
            ProfileEditPage(
                onBackButton = { navController.popBackStack() }
            )
        }

        // View image
        composable(
            "image-view/{imageId}",
            arguments = listOf(navArgument("imageId") { type = NavType.StringType })
        ) { backStackEntry ->
            val iid = backStackEntry.arguments?.getString("imageId")!!
            ZoomableImageView(database = database, iid = iid) {
                navController.popBackStack()
            }
        }

        // Open the view for a certain challenge
        composable(
            "challenge-view/{challengeId}",
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cid = backStackEntry.arguments?.getString("challengeId")!!

            Box(modifier = Modifier.fillMaxSize()) {
                FetchComponent(
                    lazyRef = { database.getChallengeById(cid) },
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    ChallengeView(it,
                        database = database,
                        user = Authenticator.authInstance.get().user!!,
                        { cid -> navController.navigate("image-view/$cid") }) {
                        navController.popBackStack()
                    }
                }
            }
        }

        // Open claim view for a given challenge
        composable(
            "claim-challenge/{challengeId}",
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cid = backStackEntry.arguments?.getString("challengeId")!!

            Box(modifier = Modifier.fillMaxSize()) {
                FetchComponent(
                    lazyRef = { database.getChallengeById(cid) },
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    ClaimChallenge(database = database, challenge = it)
                }
            }
        }

        // Settings
        composable(HiddenRoute.Settings.route) {
            SettingsPage({ navController.popBackStack() }) { navController.navigate(it.route)}
        }

        composable(HiddenRoute.AppSettings.route) {
            val viewModel = AppSettingsViewModel(container.appSettingsRepository)
            AppSettingsPage(onBack = { navController.popBackStack() }, viewModel)
        }

        composable(HiddenRoute.PrivacySettings.route) {
            val viewModel = PrivacySettingsViewModel()
            PrivacySettingsPage(onBack = { navController.popBackStack() }, viewModel)
        }
    }
}

private fun logout(authenticator: Authenticator, activity: ComponentActivity) {
    authenticator.signOut(activity).thenAccept {
        activity.replaceActivity(Intent(activity, LoginActivity::class.java))
    }
}