package com.github.geohunt.app.ui.components

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import com.github.geohunt.app.utility.findActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture

class ChallengeTest {
    private lateinit var database: FirebaseDatabase
//    val dummyChallenge = object : Challenge {
//        override val cid: String
//            get() = "1234"
//        override val author: LazyRef<User>
//            get() = MockLazyRef("1234") { TODO() }
//        override val publishedDate: LocalDateTime
//            get() = LocalDateTime.of(2010, 7, 24, 20, 54)
//        override val expirationDate: LocalDateTime?
//            get() = null
//        override val thumbnail: LazyRef<Bitmap>
//            get() = MockLazyRef("5678") { Tasks.forResult(Bitmap.createBitmap(1024, 4096, Bitmap.Config.ARGB_8888)) }
//        override val coarseLocation: Location
//            get() = TODO("Not yet implemented")
//        override val correctLocation: Location
//            get() = TODO("Not yet implemented")
//        override val claims: List<LazyRef<Claim>>
//            get() = (1..100).toList().map { i -> mockClaim(i) }
//    }

    fun mockClaim(i: Int): LazyRef<Claim> {
        return MockLazyRef(i.toString()) { TODO() }
    }

    @get:Rule
    val testRule = createComposeRule()

    @Before
    fun setupComposable() {
        FirebaseEmulator.init()
        val future = CompletableFuture<Void?>()
        testRule.setContent {
            database = FirebaseDatabase(LocalContext.current.findActivity())
            val challenge = rememberLazyRef {
                database.getChallengeById("163f921c-NQWln8MlqnVhArUIdwE")
            }
            if (challenge.value != null) {
                LaunchedEffect(null) {
                    future.complete(null)
                }
                GeoHuntTheme {
                    Challenge(challenge = challenge.value!!)
                }
            }

        }
        future.join()
    }

    @Test
    fun textsCorrectlyDisplayed() {
        testRule.onNodeWithText("Created by", substring = true).assertExists()
        testRule.onNodeWithText("Published", substring = true).assertExists()
        testRule.onNodeWithText("Time", substring = true).assertExists()
    }

    @Test
    fun publishedDateIsDisplayed() {
        testRule.onNodeWithText("Published", substring = true).assertTextContains("14/03/2023", substring = true)
    }

    @Test
    fun nullExpirationDateIsDisplayed() {
        testRule.onNodeWithText("Time", substring = true).assertTextContains("Never", substring = true)
    }

    @Test
    fun buttonIsClickable() {
        testRule.onNodeWithText("Claim", useUnmergedTree = true).onParent().assertHasClickAction()
    }
}