package com.github.geohunt.app.maps

import android.graphics.Bitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.maps.marker.Marker
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month

class GoogleMapViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val epflCoordinates = LatLng(46.51958, 6.56398)
    private val mockBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    private val mockChallengeDatabase : List<Marker> = listOf<Marker>(
        Marker(
            LatLng(46.51958, 6.56398),
            "Event 1",
            "Expires on 1 May 2024 at 19:39",
            mockBitmap,
            LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12)
        ),
        Marker(
            LatLng(46.519, 6.563),
            "Event 2",
            "Expires on 1 May 2024 at 19:39",
            mockBitmap,
            LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12)
        ),
        Marker(
            LatLng(46.5192, 6.5632),
            "Event 3",
            "Expires on 1 May 2024 at 19:39",
            mockBitmap,
            LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12)
        ),
        Marker(
            LatLng(46.5193, 6.5633),
            "Event 4",
            "Expires on 1 May 2024 at 19:39",
            mockBitmap,
            LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12)
        ),
    )


    fun init(cameraPosition: CameraPosition = CameraPosition(epflCoordinates, 9f, 0f, 0f)) {
        composeTestRule.setContent {
            GoogleMapView(
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

    @Test
    fun testClusteringAtZoomLevel7() {
        init(CameraPosition(epflCoordinates, 7f, 0f, 0f))

        composeTestRule.onNodeWithTag("Cluster for 20 markers")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("Cluster for 38 markers")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("Cluster for 42 markers")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testClusteringAtZoomLevel5() {
        init(CameraPosition(epflCoordinates, 5f, 0f, 0f))

        composeTestRule.onNodeWithTag("Cluster for 100 markers")
            .assertExists()
            .assertIsDisplayed()
    }
}