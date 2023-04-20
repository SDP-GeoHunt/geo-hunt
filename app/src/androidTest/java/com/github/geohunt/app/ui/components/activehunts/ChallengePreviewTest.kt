package com.github.geohunt.app.ui.components.activehunts

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.github.geohunt.app.R
import com.github.geohunt.app.mocks.InstantLazyRef
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class ChallengePreviewTest {
    @get:Rule
    val testRule = createComposeRule()

    private val challengeId = "98d755ad-NRDJLd1aM1I2QXK4qjD"

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private fun createTestBitmap(): Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.eiffel)?.toBitmap()!!
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
        override val difficulty: Challenge.Difficulty
            get() = TODO("Not yet implemented")
        override val likes: List<LazyRef<User>>
            get() = TODO("Not yet implemented")
        override var numberOfLikes: Int
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    @Before
    fun setupComposable() {
        testRule.setContent {
            GeoHuntTheme {
                ChallengePreview(challenge = InstantLazyRef(challengeId, dummyChallenge))
            }
        }
    }

    @Test
    fun challengeInformationAreDisplayed() {
        testRule.onNodeWithText("Expires", substring = true).assertIsDisplayed().assertTextContains("never", substring = true)
        testRule.onNodeWithText("Debug User", substring = true).assertIsDisplayed()
        testRule.onNodeWithText("Italy", substring = true).assertIsDisplayed()
    }

    @Test
    fun iconsAreDisplayed() {
        testRule.onAllNodesWithContentDescription("icon", substring = true).assertCountEquals(3)
        testRule.onNodeWithContentDescription("person", substring = true).assertIsDisplayed()
        testRule.onNodeWithContentDescription("location", substring = true).assertIsDisplayed()
        testRule.onNodeWithContentDescription("calendar", substring = true).assertIsDisplayed()
    }

    @Test
    fun imageIsDisplayed() {
        testRule.onNodeWithContentDescription("Challenge", substring = true).assertIsDisplayed().assertContentDescriptionContains(challengeId, substring = true)
    }
}