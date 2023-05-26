package com.github.geohunt.app.ui.components.navigation

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.geohunt.app.R
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.maps.GoogleMapDisplay
import com.github.geohunt.app.sensor.RequireFineLocationPermissions
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
import com.github.geohunt.app.ui.screens.bounty.ChatScreen
import com.github.geohunt.app.ui.screens.bounty_team_select.BountyTeamSelectPage
import com.github.geohunt.app.ui.screens.home.HomeScreen
import com.github.geohunt.app.ui.screens.teamleaderboard.TeamLeaderboard
import com.github.geohunt.app.ui.screens.teamprogress.TeamProgressScreen
import com.github.geohunt.app.ui.screens.userleaderboard.UserLeaderboard
import com.github.geohunt.app.ui.settings.SettingsPage
import com.github.geohunt.app.ui.settings.app_settings.AppSettingsPage
import com.github.geohunt.app.ui.settings.app_settings.AppSettingsViewModel
import com.github.geohunt.app.ui.settings.privacy_settings.PrivacySettingsPage
import com.github.geohunt.app.ui.settings.privacy_settings.PrivacySettingsViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Represents an app screen that is reachable in the navigation graph using a provided route.
 */
sealed interface Screen {
    /**
     * Returns the route that is used to navigate to this screen in the navigation graph.
     *
     * This route must be unique to avoid navigation conflicts.
     */
    val route: String
}

/**
 * Represents a top-level screen.
 *
 * Such screens are primary destinations of the application, and are represented with their
 * [icon] and [label] in the [GeoHuntNavigationBar].
 *
 * @param label A textual label shown below the selected [NavigationBarItem].
 * @param icon The icon of the screen in the [GeoHuntNavigationBar].
 */
enum class PrimaryScreen(
    val label: String?,
    val icon: @Composable () -> Unit
): Screen {
    Home(
        label = "Home",
        icon = { Icon(Icons.Default.Home, contentDescription = "Home icon") }
    ),
    Explore(
        label = "Explore",
        icon = { Icon(Icons.Outlined.Explore, contentDescription = "Explore icon") }
    ),
    Create(
        label = null, // Won't be shown since it opens in full screen
        icon = { Icon(Icons.Default.Add, contentDescription = "Create icon") }
    ),
    ActiveHunts(
        label = "Hunts",
        icon = { Icon(painterResource(id = R.drawable.target_arrow), "Hunts icon") }
    ),
    Profile(
        label = "Profile",
        icon = { Icon(Icons.Default.Person, contentDescription = "Profile icon") }
    );

    override val route: String
        get() = this.name.lowercase()
}

/**
 * Represents a secondary screen that is not present in the bottom [GeoHuntNavigationBar].
 */
enum class SecondaryScreen(override val route: String): Screen {
    BountyTeamChooser("bounty/team-select"),
    BountyTeamProgress("bounty/team-progress"),
    EditProfile("settings/profile"),
    Settings("settings"),
    AppSettings("settings/app"),
    PrivacySettings("settings/privacy"),
    Leaderboard("leaderboard"),
    CreateChallenge("create-challenge"),
    CreateBounty("create-bounty"),
    BountyClaimChallenge("bounty-claim-challenge"),
    ChallengeView("challenge-view"),
    BountyLeaderboard("bounty/leaderboard"),
    TeamChat("bounty/team-progress/chat"),
}

@Composable
fun NavigationController(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    logout: () -> Unit
) {
    val context = LocalContext.current
    val container: AppContainer = AppContainer.getInstance(LocalContext.current.applicationContext as Application)
    val onFailure : (Throwable) -> Unit = {
        Toast.makeText(context, "Something went wrong, failed to perform the operation", Toast.LENGTH_LONG).show()
        Log.e("GeoHunt", "Failure encountered: $it")
        navController.popBackStack()
    }

    NavHost(navController, startDestination = PrimaryScreen.Home.route, modifier = modifier) {
        composable(PrimaryScreen.Home.route) {
            RequireFineLocationPermissions {
                HomeScreen(
                    onUserClick = { navController.navigate("${PrimaryScreen.Profile.route}/${it.id}") },
                    onOpenMap = { /* TODO Open with challenge-centered map */ navController.navigate(PrimaryScreen.Explore.route) },
                    onOpenChallenge = { navController.navigate("challenge-view/${it.id}") },
                    onClaim = { navController.navigate("claim-challenge/${it.id}") },
                    onOpenExplore = { navController.navigate(PrimaryScreen.Explore.route) },
                    showTeamProgress = { navController.navigate("${SecondaryScreen.BountyTeamProgress.route}/${it.bid}") },
                    showTeamChooser = { navController.navigate("${SecondaryScreen.BountyTeamChooser.route}/${it.bid}") }
                )
            }
        }
        composable(PrimaryScreen.Explore.route) {
            GoogleMapDisplay(
                modifier = Modifier.fillMaxSize(),
                onFailure = {
                    Toast.makeText(context, "Something went wrong, failed to obtain location permission", Toast.LENGTH_LONG).show()
                    Log.e("GeoHunt", "Failure encountered: $it")
                    navController.popBackStack()
                }
            )
        }
        composable(SecondaryScreen.CreateChallenge.route) {
            CreateNewChallenge(
                onFailure = onFailure,
                onSuccess = {
                    navController.popBackStack()
                    navController.navigate("challenge-view/${it.id}")
                }
            )
        }

        composable(SecondaryScreen.CreateBounty.route) {
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

        composable(PrimaryScreen.ActiveHunts.route) {
            ActiveHuntsScreen(
                openExploreTab = { navController.navigate(PrimaryScreen.Home.route) },
                openChallengeView = { navController.navigate("${SecondaryScreen.ChallengeView.route}/${it.id}") },
                openBountyView = { navController.navigate("${SecondaryScreen.BountyTeamProgress.route}/${it.bid}") }
            )
        }

        // Profile
        composable(PrimaryScreen.Profile.route) {
            ProfilePage(
                openLeaderboard = { navController.navigate(SecondaryScreen.Leaderboard.route) },
                openProfileEdit = { navController.navigate(SecondaryScreen.EditProfile.route) },
                openSettings = { navController.navigate(SecondaryScreen.Settings.route) },
                onLogout = { logout() },
                openChallengeView = { navController.navigate("${SecondaryScreen.ChallengeView.route}/${it.id}") }
            )
        }

        composable(SecondaryScreen.Leaderboard.route) {
            UserLeaderboard()
        }

        composable("${PrimaryScreen.Profile.route}/{userId}", arguments = listOf(navArgument("userId") { type = NavType.StringType })) {
            it.arguments?.getString("userId")?.let {
                userId -> ProfilePage(
                    ProfilePageViewModel(
                        container.auth, container.user, container.challenges, container.follow, container.profileVisibilities, container.claims, container.score, userId
                    )
                )
            }
        }

        composable(SecondaryScreen.EditProfile.route) {
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
            SecondaryScreen.ChallengeView.route + "/{challengeId}",
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cid = backStackEntry.arguments?.getString("challengeId")!!

            ChallengeView(
                cid = cid,
                fnViewImageCallback = { navController.navigate("image-view/${URLEncoder.encode(it, StandardCharsets.UTF_8.toString())}") },
                fnClaimHuntCallback = { cid -> navController.navigate("claim-challenge/$cid") },
                onBack = { navController.popBackStack() }
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
                onSuccess = {
                    navController.popBackStack()
                    navController.navigate(SecondaryScreen.ChallengeView.route + "/$cid")
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
        composable(SecondaryScreen.Settings.route) {
            SettingsPage({ navController.popBackStack() }) { navController.navigate(it.route)}
        }

        composable(SecondaryScreen.AppSettings.route) {
            val viewModel = AppSettingsViewModel(container.appSettingsRepository)
            AppSettingsPage(onBack = { navController.popBackStack() }, viewModel)
        }

        composable(SecondaryScreen.PrivacySettings.route) {
            val viewModel = PrivacySettingsViewModel(
                userRepository = container.user,
                profileVisibilityRepository = container.profileVisibilities
            )
            PrivacySettingsPage(onBack = { navController.popBackStack() }, viewModel)
        }

        // Bounties
        composable(
            "${SecondaryScreen.BountyTeamChooser.route}/{bountyId}",
            arguments = listOf(navArgument("bountyId") { type = NavType.StringType })
        ) {
            val bid = it.arguments?.getString("bountyId")!!
            BountyTeamSelectPage(bid, onBack = { navController.popBackStack() }, onSelectedTeam = {
                navController.navigate("${SecondaryScreen.BountyTeamProgress.route}/$bid")
            })
        }

        composable(
            "${SecondaryScreen.BountyTeamProgress.route}/{bountyId}",
            arguments = listOf(navArgument("bountyId") { type = NavType.StringType })
        ) {
            val bid = it.arguments?.getString("bountyId")!!
            RequireFineLocationPermissions { TeamProgressScreen(
                onBack = { navController.popBackStack() },
                onLeaderboard = { navController.navigate("bounty/leaderboard/$bid") },
                onChat = { navController.navigate("bounty/team-progress/chat/$bid") },
                onClaim = { navController.navigate("${SecondaryScreen.BountyClaimChallenge.route}/$bid/${it.id}") },
                bountyId = bid
            ) }
        }

        composable("${SecondaryScreen.TeamChat.route}/{bountyId}",
            arguments = listOf(navArgument("bountyId") {type = NavType.StringType})
        ) {
            val bid = it.arguments?.getString("bountyId")!!
            ChatScreen(onBack = { navController.popBackStack() }, bountyId = bid)
        }

        composable(
            "${SecondaryScreen.BountyLeaderboard.route}/{bountyId}",
            arguments = listOf(navArgument("bountyId") { type = NavType.StringType })
        ) {
            val bid = it.arguments?.getString("bountyId")!!
            TeamLeaderboard(bid = bid)
        }

        // Bounties
        // Open a claim for a given bounty's challenge
        composable(
            "${SecondaryScreen.BountyClaimChallenge.route}/{bountyId}/{challengeId}",
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
                onSuccess = {
                    navController.popBackStack()
                    navController.navigate("${SecondaryScreen.ChallengeView.route}/$cid")
                }
            )
        }
    }
}
