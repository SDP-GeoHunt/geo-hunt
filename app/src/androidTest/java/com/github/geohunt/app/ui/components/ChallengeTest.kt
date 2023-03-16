package com.github.geohunt.app.ui.components

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
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
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture

class ChallengeTest {
    private lateinit var database: FirebaseDatabase

    @get:Rule
    val testRule = createComposeRule()

    @Before
    fun setupComposable() {
        FirebaseEmulator.init()
        testRule.setContent {
            database = FirebaseDatabase(LocalContext.current.findActivity())
            val challenge = rememberLazyRef {
                database.getChallengeById("163f921c-NQWln8MlqnVhArUIdwE")
            }

            if (challenge.value != null) {
                Text("Finished")

                GeoHuntTheme {
                    Challenge(challenge = challenge.value!!)
                }
            }

        }

        testRule.waitUntil {
            testRule.onAllNodesWithText("Finished")
                .fetchSemanticsNodes().size == 1
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