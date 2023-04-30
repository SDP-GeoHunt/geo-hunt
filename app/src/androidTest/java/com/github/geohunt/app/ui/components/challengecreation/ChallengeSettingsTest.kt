package com.github.geohunt.app.ui.components.challengecreation

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import java.io.File
import java.time.LocalDate

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
                ChallengeSettings(viewModel = viewModel<CreateChallengeViewModel>(factory = CreateChallengeViewModel.Factory).apply {
                    this.withPhoto(File.createTempFile("TEST_", ".webm")) {}
                    this.startLocationUpdate()
                })
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