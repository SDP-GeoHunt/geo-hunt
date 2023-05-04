package com.github.geohunt.app.ui.components

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.ui.components.navigation.NavigationController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Test
    fun testImageViewWithNavController() {

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavigationController(navController = navController) { }

            LaunchedEffect(true) {
                navController.navigate("image-view/http://10.0.2.2:9199/geohunt-1.appspot.com/images/challenges-images.png")
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("image-view-image-arf4ae56f4a1")
            .assertIsDisplayed()
    }
}