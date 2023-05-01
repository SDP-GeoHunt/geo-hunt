@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.geohunt.app.ui.components

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.geohunt.app.R
import com.github.geohunt.app.ConstantStrings
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.mocks.*
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.challenge.ChallengeView
import com.github.geohunt.app.ui.components.challenge.ChallengeViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var appContainer : AppContainer

    private fun createViewModel() : ChallengeViewModel {
        return ChallengeViewModel.createOf(appContainer)
    }

    @Before
    fun setup() {
        appContainer = AppContainer.getEmulatedFirebaseInstance(
            androidx.test.core.app.ApplicationProvider.getApplicationContext() as Application
        )
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
    
    @Test
    fun testChallenge1() {
        var route = ""
        val vm = createViewModel()

        composeTestRule.setContent {
            ChallengeView(
                cid = "163f921c-ML2eCQ52mAQlvCEQZ2n",
                fnViewImageCallback = { route = "image-view/$it" },
                fnClaimHuntCallback = { route = "claim/$it" } ,
                fnGoBackBtn = { route = ".." },
                viewModel = vm)
        }

        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("challenge-main-image")
                .fetchSemanticsNodes()
                .size == 1
        }

        composeTestRule.onNodeWithContentDescription("Challenge Image")
            .assertIsDisplayed()
            .performClick()
        assertThat(route, equalTo("image-view/http://10.0.2.2:9199/geohunt-1.appspot.com/images/challenges-images.png"))

        composeTestRule.onNodeWithText("Here's Johny")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Follow")
            .performScrollTo()
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithText("Join Hunt")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithTag("description-more-btn")
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText("more…"))
            .performClick()
    }

}