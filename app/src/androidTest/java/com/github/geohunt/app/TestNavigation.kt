package com.github.geohunt.app

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavDestination
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import com.github.geohunt.app.ui.components.NavigationBar
import com.github.geohunt.app.ui.components.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TestNavigation {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController
    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            val navigator = ComposeNavigator()
            navController.navigatorProvider.addNavigator(navigator)

            navController.createGraph(Route.Home.route, builder = {
                for (route in Route.values()) this.addDestination(NavDestinationBuilder(navigator, route.route).build())
                println("OKKKKKK GOOOD")
            })

            NavigationBar(navController = navController)
        }
    }

    @Test
    fun correctIconIsActive() {
        println("BEGINNNGJSDHNFDS")
        for (route in Route.values()) {
            navController.navigate(route.route)
            composeTestRule.onNode(hasTestTag("navbtn-" + route.route)).assertIsSelected()
            // check all other buttons that they are disabled
            for(route2 in Route.values()) {
                if (route != route2) composeTestRule.onNode(hasTestTag("navbtn-" + route2.route)).assertIsNotSelected()
            }
        }
    }
}