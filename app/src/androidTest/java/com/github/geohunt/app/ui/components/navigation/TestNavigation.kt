package com.github.geohunt.app.components.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.geohunt.app.ui.components.navigation.NavigationBar
import com.github.geohunt.app.ui.components.navigation.NavigationController
import com.github.geohunt.app.ui.components.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TestNavigation {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavigationController(navController = navController)
            NavigationBar(navController = navController)
        }
    }

    @Test
    fun startRouteIsHome() {
        assert(Route.Home.route == navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun clickingOnButtonSelectsIt() {
        for (route in Route.values()) {
            val node = composeTestRule.onNode(hasTestTag("navbtn-" + route.route))
            node.performClick()
            node.assertIsSelected()
        }
    }

    @Test
    fun clickingOnButtonRedirects() {
        for (route in Route.values()) {
            val node = composeTestRule.onNode(hasTestTag("navbtn-" + route.route))
            node.performClick()
            assert(navController.currentBackStackEntry?.destination?.route == route.route)
        }
    }
}