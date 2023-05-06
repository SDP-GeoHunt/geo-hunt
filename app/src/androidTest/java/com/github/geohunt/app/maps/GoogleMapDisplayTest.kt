package com.github.geohunt.app.maps

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.maps.marker.Marker
import com.github.geohunt.app.maps.marker.MarkerInfoWindowContent
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
class GoogleMapDisplayTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val epflCoordinates = LatLng(46.51958, 6.56398)
    private var mockTestChallengeDatabase = mutableListOf<Marker>()

    @Before
    fun initializeMockChallengeDatabase() {
        for (i in 1..2) {
            mockTestChallengeDatabase.add(Marker(
                title = "Event $i",
                image = "",
                coordinates = LatLng(46.51958 + i * 0.01, 6.56398 + i * 0.01),
                expiryDate = LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12))
            )
        }
        mockTestChallengeDatabase.add(Marker(
            title = "Event 3",
            image = "https://picsum.photos/300/300",
            coordinates = LatLng(46.5195, 6.5634),
            expiryDate = LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12))
        )
    }

    @Test
    fun testMapIsDisplayed() {
        val cameraPosition = CameraPosition(epflCoordinates, 12f, 0f, 0f)
        composeTestRule.setContent {
            GoogleMapDisplay(
                Modifier.testTag("Maps"),
                cameraPosition = cameraPosition,
            )
        }

        composeTestRule.onNodeWithTag("Maps")
            .assertExists()
    }

    @Test
    fun markerInfoWindowContentIsDisplayedCorrectly() {
        composeTestRule.setContent {
            MarkerInfoWindowContent(challenge = mockTestChallengeDatabase[0])
        }

        composeTestRule
            .onNodeWithContentDescription("Marker Image")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Marker title")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Marker expiry date")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("2024-05-01T19:39:12")
            .assertIsDisplayed()
    }

    @Test
    fun markerInfoWindowImageFetchingDoesNotThrowException() {
        composeTestRule.setContent {
            MarkerInfoWindowContent(challenge = mockTestChallengeDatabase[2])
        }

        composeTestRule
            .onNodeWithContentDescription("Marker Image")
            .assertExists()
    }
}