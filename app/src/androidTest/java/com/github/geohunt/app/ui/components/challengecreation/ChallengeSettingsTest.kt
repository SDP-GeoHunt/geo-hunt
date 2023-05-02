package com.github.geohunt.app.ui.components.challengecreation

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.model.Challenge.Difficulty
import com.github.geohunt.app.model.database.api.Location
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChallengeSettingsTest {
    @get:Rule
    val testRule = createComposeRule()

    // Random location that does not represent the state of the project
    private val mockedLocation = Location(51.27658527780932, 30.21759376638171)

    @Before
    fun setup() {
        AppContainer.getEmulatedFirebaseInstance(
            androidx.test.core.app.ApplicationProvider.getApplicationContext() as Application
        )
    }

    @Test
    fun settingsTextsAreDisplayed() {
        val flow = MutableSharedFlow<Location>()

        LocationRepository.DefaultLocationFlow.mocked(flow).use {
            testRule.setContent {
                ChallengeSettings(
                    difficulty = Difficulty.MEDIUM,
                    setDifficultyCallback = {},
                    expirationDate = null,
                    setExpirationDate = {}
                )
            }

            runBlocking {
                flow.emit(mockedLocation)
            }

            testRule.onNodeWithText("Difficulty", substring = true, useUnmergedTree = true)
                .assertIsDisplayed()

            testRule.onNodeWithText("Date", substring = true, useUnmergedTree = true)
                .assertIsDisplayed()
        }
    }
}