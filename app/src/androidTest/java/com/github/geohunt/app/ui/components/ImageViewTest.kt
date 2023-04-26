package com.github.geohunt.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.geohunt.app.R
import com.github.geohunt.app.mocks.BaseMockDatabase
import com.github.geohunt.app.mocks.InstantLazyRef
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.ui.components.navigation.NavigationController
import com.google.android.gms.tasks.Tasks
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }

    @Test
    fun testImageViewWithNavController() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val profilePicture = createTestBitmap(context)

        val database = object : BaseMockDatabase() {
            override fun getImageById(iid: String): LazyRef<Bitmap> {
                return if (iid == "image-arf4ae56f4a1") {
                    InstantLazyRef(iid, profilePicture)
                } else {
                    MockLazyRef(iid) {
                        Tasks.forException(IllegalStateException())
                    }
                }
            }
        }

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavigationController(navController = navController, database = database)

            LaunchedEffect(true) {
                navController.navigate("image-view/image-arf4ae56f4a1")
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("image-view-image-arf4ae56f4a1")
            .assertIsDisplayed()
    }
}