package com.github.geohunt.app

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.PictureImage
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

class ChallengeTest {
    private val dummyChallenge = object : Challenge {
        override val cid: String
            get() = "1234"
        override val uid: String
            get() = "5678"
        override val published: LocalDateTime
            get() = LocalDateTime.of(2010, 7, 24, 20, 54)
        override val expirationDate: LocalDateTime?
            get() = LocalDateTime.of(2024, 3, 8, 18, 12)
        override val thumbnail: PictureImage
            get() = object : PictureImage {
                override val iid: String
                    get() = "4321"
                override val bitmap: Bitmap?
                    get() = null

                override fun load(): CompletableFuture<Bitmap> {
                    return CompletableFuture.completedFuture(bitmap)
                }
                override fun save(): CompletableFuture<Void> {
                    TODO("Not yet implemented")
                }
            }
        override val coarseLocation: Location
            get() = TODO("Not yet implemented")
        override val correctLocation: Location
            get() = TODO("Not yet implemented")
        override val claims: List<String>
            get() = (1..100).toList().map { i -> i.toString() }
    }
    @get:Rule
    val testRule = createComposeRule()

    @Before
    fun setupComposable() {
        testRule.setContent {
            GeoHuntTheme {
                Challenge(challenge = dummyChallenge)
            }
        }
    }

    @Test
    fun textsCorrectlyDisplayed() {
        testRule.onNodeWithText("Created by", substring = true).assertExists()
        testRule.onNodeWithText("Published", substring = true).assertExists()
        testRule.onNodeWithText("Time", substring = true).assertExists()
    }

    @Test
    fun publishedDateIsDisplayed() {
        testRule.onNodeWithText("Published", substring = true).assertTextContains("24/07/2010", substring = true)
    }

    @Test
    fun buttonIsClickable() {
        testRule.onNodeWithText("Claim", useUnmergedTree = true).onParent().assertHasClickAction()
    }
}