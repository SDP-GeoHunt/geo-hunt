package com.github.geohunt.app.maps

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.data.repository.LocationRepositoryInterface
import com.github.geohunt.app.maps.marker.Marker
import com.github.geohunt.app.maps.marker.MarkerInfoWindowContent
import com.github.geohunt.app.mocks.MockChallengeRepository
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.screens.maps.MapsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    private val mockLocation = Location(46.51958, 6.56398)
    private val mockLocationRepo = object : LocationRepositoryInterface {
        override fun getLocations(coroutineScope: CoroutineScope): Flow<Location> {
            return flowOf(mockLocation)
        }
    }

    private val mockChallengeRepository = MockChallengeRepository()

    private fun mockViewModel(): MapsViewModel {
        return MapsViewModel(
            challengeRepository = mockChallengeRepository,
            locationRepository = mockLocationRepo,
        )
    }

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
    fun testMapIsLoadingCorrectly() {
        composeTestRule.setContent {
            GoogleMapDisplay(
                Modifier.testTag("Maps"),
                setCameraPosition = CameraPosition(epflCoordinates, 10f, 0f, 0f),
                viewModel = mockViewModel(),
            )
        }

        composeTestRule
            .onNodeWithTag("Maps")
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
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag("Marker expiry date")
            .assertIsDisplayed()
    }

    @Test
    fun markerInfoWindowImageFetchingDoesNotThrowException() {
        composeTestRule.setContent {
            MarkerInfoWindowContent(challenge = mockTestChallengeDatabase[2])
        }

        composeTestRule
            .onAllNodesWithTag("Marker image")
            .assertAny(hasClickAction())
    }
}
