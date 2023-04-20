package com.github.geohunt.app.ui.components.activehunts

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.github.geohunt.app.R
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.mocks.InstantLazyRef
import com.github.geohunt.app.mocks.MockAuthenticator
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.model.database.api.*
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.ui.components.navigation.Route
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class ActiveHuntsTest {
    @get:Rule
    val testRule = createComposeRule()

    var exploreCallbackCalled = false
    var imageViewCallbackCalled = false

    private fun setupComposable(challenges: List<LazyRef<Challenge>>) {
        FirebaseEmulator.init()
        exploreCallbackCalled = false
        imageViewCallbackCalled = false
        testRule.setContent {
            GeoHuntTheme {
                ActiveHunts(challenges = challenges, { exploreCallbackCalled = true }) {
                    imageViewCallbackCalled = true
                }
            }
        }
    }

    @Test
    fun titleTextIsDisplayed() {
        setupComposable(listOf())
        testRule.onNodeWithText("Active hunts").assertIsDisplayed()
    }

    @Test
    fun textIsDisplayedOnEmptyChallengeList() {
        setupComposable(listOf())
        testRule.onNodeWithText("No challenges yet", substring = true).assertIsDisplayed()
        testRule.onNodeWithText("Search", substring = true, useUnmergedTree = true).assertIsDisplayed()
    }

    private val dummyChallenge = object : Challenge {
        override val cid: String
            get() = challengeId
        override val author: LazyRef<User>
            get() = InstantLazyRef("user", MockUser(displayName = "Debug User"))
        override val publishedDate: LocalDateTime
            get() = LocalDateTime.of(10, 10, 10, 10, 10)
        override val expirationDate: LocalDateTime?
            get() = null
        override val thumbnail: LazyRef<Bitmap>
            get() = InstantLazyRef("image", createTestBitmap())
        override val coarseLocation: Location
            get() = TODO("Not yet implemented")
        override val correctLocation: Location
            get() = TODO("Not yet implemented")
        override val claims: List<LazyRef<Claim>>
            get() = TODO("Not yet implemented")
        override val numberOfActiveHunters: Int
            get() = 0
        override val description: String?
            get() = TODO("Not yet implemented")
        override val difficulty: Challenge.Difficulty
            get() = TODO("Not yet implemented")
        override val likes: List<LazyRef<User>>
            get() = TODO("Not yet implemented")
    }

    private val challengeId = "dummy"

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private fun createTestBitmap(): Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.eiffel)?.toBitmap()!!
    }

    @Test
    fun atLeastOneChallengesIsDisplayed() {
        val challenge: LazyRef<Challenge> = InstantLazyRef("dummyRef", dummyChallenge)
        val challenges = listOf(challenge, challenge, challenge)
        setupComposable(challenges)

        testRule.onNodeWithTag("challenge_row")
                .onChildren()
                .filter(hasContentDescription("Challenge ${dummyChallenge.cid}"))
                //We use assertAny to make sure there is at least one node filling the condition
                .assertAny(hasContentDescription("Challenge ${dummyChallenge.cid}"))

        testRule.onNodeWithTag("challenge_row")
            .onChildren()
            .filter(hasClickAction())
            .onFirst()
            .performClick()
        assertThat(imageViewCallbackCalled, equalTo(true))
    }

    @Test
    fun callbackIsCalledByButton() {
        setupComposable(listOf())

        testRule.onNodeWithText("Search nearby challenges", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()

        assertThat(exploreCallbackCalled, equalTo(true))
        assertThat(imageViewCallbackCalled, equalTo(false))
    }
}