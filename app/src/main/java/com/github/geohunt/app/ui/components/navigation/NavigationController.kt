package com.github.geohunt.app.ui.components.navigation

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.material.Button
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.geohunt.app.R
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.ui.components.ZoomableImageView
import com.github.geohunt.app.ui.components.bounties.AdminBountyPage
import com.github.geohunt.app.ui.components.bounties.BountyClaimChallenge
import com.github.geohunt.app.ui.components.bounties.CreateNewBounty
import com.github.geohunt.app.ui.components.challenge.ChallengeView
import com.github.geohunt.app.ui.components.challengecreation.CreateChallengeViewModel
import com.github.geohunt.app.ui.components.challengecreation.CreateNewChallenge
import com.github.geohunt.app.ui.components.claims.ClaimChallenge
import com.github.geohunt.app.ui.components.profile.ProfilePage
import com.github.geohunt.app.ui.components.profile.ProfilePageViewModel
import com.github.geohunt.app.ui.components.profile.edit.ProfileEditPage
import com.github.geohunt.app.ui.screens.activehunts.ActiveHuntsScreen
import com.github.geohunt.app.ui.screens.bounty_team_select.BountyTeamSelectPage
import com.github.geohunt.app.ui.screens.home.HomeScreen
import com.github.geohunt.app.ui.screens.userleaderboard.UserLeaderboard
import com.github.geohunt.app.ui.settings.SettingsPage
import com.github.geohunt.app.ui.settings.app_settings.AppSettingsPage
import com.github.geohunt.app.ui.settings.app_settings.AppSettingsViewModel
import com.github.geohunt.app.ui.settings.privacy_settings.PrivacySettingsPage
import com.github.geohunt.app.ui.settings.privacy_settings.PrivacySettingsViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

typealias ComposableFun = @Composable () -> Unit

interface Route {
    val route: String
}

enum class VisibleRoute(val titleStringId: Int,
                        override val route: String,
                        val icon: ComposableFun): Route {

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
    BountyTeamChooser("bounty/team-select"),
    BountyView("bounty/view"),
    EditProfile("settings/profile"),
    Settings("settings"),
    AppSettings("settings/app"),
    PrivacySettings("settings/privacy"),
    Leaderboard("leaderboard"),
    CreateChallenge("create-challenge"),
    CreateBounty("create-bounty"),
    BountyClaimChallenge("bounty-claim-challenge"),
    ChallengeView("challenge-view"),

}

@Composable
fun NavigationController(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    logout: () -> Any
) {
    val context = LocalContext.current
    val container: AppContainer = AppContainer.getInstance(LocalContext.current.applicationContext as Application)
    val onFailure : (Throwable) -> Unit = {
        Toast.makeText(context, "Something went wrong, failed to perform the operation", Toast.LENGTH_LONG).show()
        Log.e("GeoHunt", "Failure encountered: $it")
        navController.popBackStack()
    }

    NavHost(navController, startDestination = VisibleRoute.Home.route, modifier = modifier) {
        composable(VisibleRoute.Home.route) {
            HomeScreen(navigate = { navController.navigate(it) })
        }
        composable(VisibleRoute.Explore.route) {
            val epflCoordinates = LatLng(46.519585, 6.5684919)
            val epflCameraPosition = CameraPosition(epflCoordinates, 15f, 0f, 0f)
            GoogleMapDisplay(
                modifier = Modifier.fillMaxSize(),
                cameraPosition = epflCameraPosition
            )
        }
        composable(HiddenRoute.CreateChallenge.route) {
            CreateNewChallenge(
                onFailure = onFailure,
                onSuccess = {
                    navController.popBackStack()
                    navController.navigate("challenge-view/${it.id}")
                }
            )
        }

        composable(HiddenRoute.CreateBounty.route) {
            CreateNewBounty(
                onFailure = onFailure,
                onSuccess = { bounty ->
                    navController.popBackStack()
                    navController.navigate("bounty-admin-page/${bounty.bid}")
                }
            )
        }

        composable(
            "create-challenge-bounty/{bountyId}",
            arguments = listOf(navArgument("bountyId") { type = NavType.StringType })
        ) {
            val bid = it.arguments?.getString("bountyId")!!
            CreateNewChallenge(
                onFailure = onFailure,
                onSuccess = { navController.popBackStack() },
                viewModel = viewModel(factory = CreateChallengeViewModel.BountyFactory(bid))
            )
        }

        composable(VisibleRoute.ActiveHunts.route) {
            ActiveHuntsScreen(
                openExploreTab = { navController.navigate(VisibleRoute.Explore.route) }
            )
        }

        // Profile
        composable(VisibleRoute.Profile.route) {
            ProfilePage(
                openLeaderboard = { navController.navigate(HiddenRoute.Leaderboard.route) },
                openProfileEdit = { navController.navigate(HiddenRoute.EditProfile.route) },
                openSettings = { navController.navigate(HiddenRoute.Settings.route) },
                onLogout = { logout() }
            )
        }

        composable(HiddenRoute.Leaderboard.route) {
            UserLeaderboard()
        }

        composable("${VisibleRoute.Profile.route}/{userId}", arguments = listOf(navArgument("userId") { type = NavType.StringType })) {
            it.arguments?.getString("userId")?.let {
                userId -> ProfilePage(
                    ProfilePageViewModel(
                        container.auth, container.user, container.challenges, container.follow, container.profileVisibilities, container.claims, container.score, userId
                    )
                )
            }
        }

        composable(HiddenRoute.EditProfile.route) {
            ProfileEditPage(
                onBackButton = { navController.popBackStack() }
            )
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
                onFailure = onFailure,
                onClaimSubmitted = {
                    navController.popBackStack()
                    navController.navigate("challenge-view/$cid")
                }
            )
        }

        // Bounty
        composable(
            "bounty-admin-page/{bountyId}",
            arguments = listOf(navArgument("bountyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bid = backStackEntry.arguments?.getString("bountyId")!!
            AdminBountyPage(bid, onFailure = onFailure, onCreateChallenge = {
                navController.navigate("create-challenge-bounty/$bid")
            })
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
            val viewModel = PrivacySettingsViewModel(
                userRepository = container.user,
                profileVisibilityRepository = container.profileVisibilities
            )
            PrivacySettingsPage(onBack = { navController.popBackStack() }, viewModel)
        }

        // Bounties
        composable(
            "${HiddenRoute.BountyTeamChooser.route}/{bountyId}",
            arguments = listOf(navArgument("bountyId") { type = NavType.StringType })
        ) {
            val bid = it.arguments?.getString("bountyId")!!
            BountyTeamSelectPage(bid, onBack = { navController.popBackStack() }, onSelectedTeam = {})
        }

        composable(
            "${HiddenRoute.BountyView.route}/{bountyId}",
            arguments = listOf(navArgument("bountyId") { type = NavType.StringType })
        ) {
            val bid = it.arguments?.getString("bountyId")!!
            Text(text = "ok")
        }

        // Bounties
        // Open a claim for a given bounty's challenge
        composable(
            "${HiddenRoute.BountyClaimChallenge.route}/{bountyId}/{challengeId}",
            arguments = listOf(
                navArgument("bountyId") { type = NavType.StringType },
                navArgument("challengeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bid = backStackEntry.arguments?.getString("bountyId")!!
            val cid = backStackEntry.arguments?.getString("challengeId")!!

            BountyClaimChallenge(
                bid = bid,
                cid = cid,
                onFailure = {
                    Toast.makeText(context, "Something went wrong, failed to create challenge", Toast.LENGTH_LONG).show()
                    Log.e("GeoHunt", "Fail to create challenge: $it")
                    navController.popBackStack()
                },
                onClaimSubmitted = {
                    navController.popBackStack()
                    navController.navigate("${HiddenRoute.ChallengeView.route}/$cid")
                }
            )
        }
    }
}
