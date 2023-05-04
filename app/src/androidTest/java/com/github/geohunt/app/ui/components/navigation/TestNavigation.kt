package com.github.geohunt.app.ui.components.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TestNavigation {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavigationController(navController = navController) { }
            NavigationBar(navController = navController)
        }
    }

    @Test
    fun startRouteIsHome() {
        assert(VisibleRoute.Home.route == navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun clickingOnButtonSelectsIt() {
        for (route in VisibleRoute.values()) {
            // Skip Route.Create because too hard to test
            // Skip Maps because of issue
            if (route == VisibleRoute.Create || route == VisibleRoute.Explore) {
                continue
            }

            val node = composeTestRule.onNode(hasTestTag("navbtn-" + route.route))
            node.performClick()
            node.assertIsSelected()
        }
    }

    @Test
    fun clickingOnButtonRedirects() {
        for (route in VisibleRoute.values()) {
            // Skip Route.Create because too hard to test
            if (route == VisibleRoute.Create || route == VisibleRoute.ActiveHunts || route == VisibleRoute.Profile) {
                continue
            }

            val node = composeTestRule.onNode(hasTestTag("navbtn-" + route.route))
            node.performClick()
            composeTestRule.waitForIdle()
            assert(navController.currentBackStackEntry?.destination?.route == route.route)
        }
    }
}