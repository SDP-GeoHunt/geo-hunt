package com.github.geohunt.app.ui.components.navigation

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material.icons.sharp.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.geohunt.app.R
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.maps.GoogleMapDisplay
import com.github.geohunt.app.ui.components.ZoomableImageView
import com.github.geohunt.app.ui.components.challenge.ChallengeView
import com.github.geohunt.app.ui.components.challengecreation.CreateNewChallenge
import com.github.geohunt.app.ui.components.claims.ClaimChallenge
import com.github.geohunt.app.ui.components.profile.ProfilePage
import com.github.geohunt.app.ui.components.profile.edit.ProfileEditPage
import com.github.geohunt.app.ui.screens.activehunts.ActiveHuntsScreen
import com.github.geohunt.app.ui.screens.home.HomeScreen
import com.github.geohunt.app.ui.components.profile.ProfilePageViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

typealias ComposableFun = @Composable () -> Unit

enum class Route(val titleStringId: Int, val route: String, val icon: ComposableFun) {

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

enum class HiddenRoutes(val route: String) {
    EditProfile("settings/profile"),
    Leaderboard("leaderboard")
}

@Composable
fun NavigationController(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    logout: () -> Any
) {
    val context = LocalContext.current
    val appContainer = AppContainer.getInstance(context.applicationContext as Application)

    NavHost(navController, startDestination = Route.Home.route, modifier = modifier) {
        composable(Route.Home.route) {
            HomeScreen()
        }
        composable(Route.Explore.route) {
            val epflCoordinates = LatLng(46.519585, 6.5684919)
            val epflCameraPosition = CameraPosition(epflCoordinates, 15f, 0f, 0f)
            GoogleMapDisplay(
                modifier = Modifier.fillMaxSize(),
                cameraPosition = epflCameraPosition
            )
        }
        composable(Route.Create.route) {
            CreateNewChallenge(
                onFailure = {
                    Toast.makeText(context, "Something went wrong, failed to create challenge", Toast.LENGTH_LONG).show()
                    Log.e("GeoHunt", "Fail to create challenge: $it")
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                    navController.navigate("challenge-view/${it.id}")
                }
            )
        }

        composable(Route.ActiveHunts.route) {
            ActiveHuntsScreen(
                openExploreTab = { navController.navigate(Route.Explore.route) }
            )
        }

        // Profile
        composable(Route.Profile.route) {
            ProfilePage(
                openLeaderboard = { navController.navigate(HiddenRoutes.Leaderboard.route) },
                openProfileEdit = { navController.navigate(HiddenRoutes.EditProfile.route) },
                onLogout = { logout() }
            )
        }

        composable("${Route.Profile.route}/{userId}", arguments = listOf(navArgument("userId") { type = NavType.StringType })) {
            it.arguments?.getString("userId")?.let {
                userId -> ProfilePage(ProfilePageViewModel(
                    appContainer.auth, appContainer.user, appContainer.challenges, appContainer.follow, userId
                ))
            }
        }

        composable(HiddenRoutes.EditProfile.route) {
            ProfileEditPage(onBackButton = { navController.popBackStack() })
        }

        // View image
        composable(
            "image-view/{imageUrl}",
            arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("imageUrl")!!
            ZoomableImageView(url = url) {
                navController.popBackStack()
            }
        }

        // Open the view for a certain challenge
        composable(
            "challenge-view/{challengeId}",
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cid = backStackEntry.arguments?.getString("challengeId")!!

            ChallengeView(
                cid = cid,
                fnViewImageCallback = { navController.navigate("image-view/${URLEncoder.encode(it, StandardCharsets.UTF_8.toString())}") },
                fnClaimHuntCallback = { cid -> navController.navigate("claim-challenge/$cid") },
                fnGoBackBtn = { navController.popBackStack() }
            )
        }

        // Open claim view for a given challenge
        composable(
            "claim-challenge/{challengeId}",
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cid = backStackEntry.arguments?.getString("challengeId")!!
            ClaimChallenge(
                cid = cid,
                onFailure = {
                    Toast.makeText(context, "Something went wrong, failed to create challenge", Toast.LENGTH_LONG).show()
                    Log.e("GeoHunt", "Fail to create challenge: $it")
                    navController.popBackStack()
                },
                onClaimSubmitted = {
                    navController.popBackStack()
                    navController.navigate("challenge-view/$cid")
                }
            )
        }
    }
}