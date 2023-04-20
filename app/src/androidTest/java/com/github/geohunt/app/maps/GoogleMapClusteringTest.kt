package com.github.geohunt.app.maps

import android.graphics.Bitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.maps.marker.Marker
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month

class GoogleMapClusteringTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val epflCoordinates = LatLng(46.51958, 6.56398)
    private val mockTestBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    private var mockTestChallengeDatabase : List<Marker> = mutableListOf()

    @Before
    fun initMockChallengeDatabase() {
        val mockChallengeDatabase =  mutableListOf<Marker>()

        for (i in 1..100) {
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
    fun testClusteringAtZoomLevel8() {
        init(CameraPosition(epflCoordinates, 8f, 0f, 0f))

        composeTestRule.onNodeWithTag("Cluster for 10 markers")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("Cluster for 19 markers")
            .assertCountEquals(4)[0]
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("Cluster for 14 markers")
            .assertExists()
            .assertIsDisplayed()
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