package com.github.geohunt.app.maps

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.maps.marker.Marker
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDateTime
import java.time.Month

@RunWith(JUnit4::class)
class GoogleMapTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val epflCoordinates = LatLng(46.51958, 6.56398)
    private var mockTestChallengeDatabase : SnapshotStateList<Marker> = mutableStateListOf()

    private fun initializeMockChallengeDatabase() {
        val mockChallengeDatabase =  mutableStateListOf<Marker>()

        for (i in 1..3) {
            mockChallengeDatabase.add(Marker(
                title = "Event $i",
                image = "",
                coordinates = LatLng(46.51958 + i * 0.01, 6.56398 + i * 0.01),
                expiryDate = LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12))
            )
        }

        mockTestChallengeDatabase = mockChallengeDatabase
    }

    @Before
    fun init() {
        initializeMockChallengeDatabase()

        val cameraPosition = CameraPosition(epflCoordinates, 9f, 0f, 0f)
        composeTestRule.setContent {
            injectMockChallenges(mockTestChallengeDatabase)

            GoogleMapDisplay(
                Modifier.testTag("Maps"),
                cameraPosition = cameraPosition,
            )
        }
    }

    @Test
    fun testMapIsDisplayed() {
        composeTestRule.onNodeWithTag("Maps")
            .assertExists()
    }

    @Test
    fun testThreeMockMarkersAreDisplayedAndClickable() {
        init()

        composeTestRule.onNodeWithContentDescription("Marker Icon for Event 1")
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule.onNodeWithContentDescription("Marker Icon for Event 2")
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule.onNodeWithContentDescription("Marker Icon for Event 3")
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }
}
