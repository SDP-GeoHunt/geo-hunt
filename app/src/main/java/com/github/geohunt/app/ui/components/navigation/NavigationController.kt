package com.github.geohunt.app.ui.components.navigation

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
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
import com.github.geohunt.app.maps.GoogleMapView
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
import com.google.maps.android.compose.CameraPositionState

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

@Composable
fun NavigationController(
    navController: NavHostController,
    database: Database,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authenticator = Authenticator.authInstance.get()
    val activity: ComponentActivity = LocalContext.current.findActivity() as ComponentActivity

    NavHost(navController, startDestination = Route.Home.route, modifier = modifier) {
        composable(Route.Home.route) {
            Column {
                Button(onClick = {
                    authenticator.signOut(activity)
                }) {
                    Text("Sign out")
                }

                Button(onClick = {
                    navController.navigate("challenge-view/98d755ad-NRrhrThHEiOx3ph_VWP")
                }) {
                    Text(text = "Open challenge view")
                }
            }
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
            CreateNewChallenge(
                database = database,
                onChallengeCreated = { challenge ->
                    navController.popBackStack()
                    navController.navigate("challenge-view/${challenge.cid}")
                },
                onFailure = {
                    Toast.makeText(context, "Something went wrong, failed to create challenge", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }
            )
        }
        composable(Route.ActiveHunts.route) {
            val user = Authenticator.authInstance.get().user

            if (user == null) {
                Text("You are not logged in. Weird :(")
            } else {
                ActiveHunts(id = user.uid, database) {
                    navController.navigate(Route.Explore.route)
                }
            }
        }

        // Profile
        composable(Route.Profile.route) {
            val user = Authenticator.authInstance.get().user

            if (user == null) {
                Text("You are not logged in. Weird :(")
            } else {
                ProfilePage(id = user.uid, database)
            }
        }

        composable(
            "${Route.Profile.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            it.arguments?.getString("userId")?.let { it1 -> ProfilePage(id = it1, database) }
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
                    ChallengeView(it, { cid -> navController.navigate("image-view/$cid") }) {
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
    }
}