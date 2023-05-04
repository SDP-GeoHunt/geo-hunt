package com.github.geohunt.app.maps

/*
import android.graphics.Bitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.maps.marker.Marker
import com.github.geohunt.app.maps.marker.MarkerInfoBox
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month

class GoogleMapTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val epflCoordinates = LatLng(46.51958, 6.56398)
    private val mockTestBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    private var mockTestChallengeDatabase : List<Marker> = mutableListOf()

    @Before
    fun initMockChallengeDatabase() {
        val mockChallengeDatabase =  mutableListOf<Marker>()

        for (i in 1..3) {
            mockChallengeDatabase.add(Marker(
                LatLng(46.51958 + i * 0.01, 6.56398 + i * 0.01),
                "Event $i",
                "Expires on 1 May 2024 at 19:39",
                mockTestBitmap,
                LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12))
            )
        }

        mockTestChallengeDatabase = mockChallengeDatabase
    }

    fun init(cameraPosition: CameraPosition = CameraPosition(epflCoordinates, 9f, 0f, 0f)) {
        composeTestRule.setContent {
            loadChallenges(mockTestChallengeDatabase)

            GoogleMapDisplay(
                Modifier.testTag("Maps"),
                cameraPosition = cameraPosition,
            )
        }
    }

    @Test
    fun testMapIsDisplayed() {
        init()
        composeTestRule.onNodeWithTag("Maps")
            .assertExists()
    }

    @Test
    fun testMarkerInfoBoxRendersChallengeInformationCorrectly() {
        val marker = mockTestChallengeDatabase[0]

        composeTestRule.setContent {
            loadChallenges(mockTestChallengeDatabase)
            GoogleMapDisplay(
                cameraPosition = CameraPosition(epflCoordinates, 9f, 0f, 0f),
            )

            MarkerInfoBox(
                marker = marker,
            )
        }
        composeTestRule.onNodeWithTag("MarkerInfoBox")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(marker.title)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(marker.expiryDate.toString())
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testClickOnFirstThreeMockMarkersAtZoomLevel10() {
        init(CameraPosition(epflCoordinates, 10f, 0f, 0f))
        composeTestRule.onNodeWithContentDescription("Marker Icon for Event 1")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Marker Icon for Event 2")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Marker Icon for Event 3")
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }
}
*/

