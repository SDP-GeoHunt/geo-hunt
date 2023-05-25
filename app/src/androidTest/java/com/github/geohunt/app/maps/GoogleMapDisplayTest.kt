package com.github.geohunt.app.maps

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.LocationRepositoryInterface
import com.github.geohunt.app.maps.marker.Marker
import com.github.geohunt.app.maps.marker.MarkerInfoWindowContent
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.User
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

    @get:Rule
    val fineLocationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val coarseLocationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private val epflCoordinates = LatLng(46.51958, 6.56398)
    private var mockTestChallengeDatabase = mutableListOf<Marker>()

    private val mockLocation = Location(46.51958, 6.56398)
    private val mockLocationRepo = object : LocationRepositoryInterface {
        override fun getLocations(coroutineScope: CoroutineScope): Flow<Location> {
            return flowOf(mockLocation)
        }
    }

    val mockRepositoryChallenge = Challenge(
        id = "Event 3",
        authorId = "test",
        photoUrl = "",
        location = Location(46.5195, 6.5634),
        publishedDate = LocalDateTime.of(LocalDateTime.now().year + 10, Month.MAY, 1, 19, 39, 12),
        difficulty = Challenge.Difficulty.EASY,
        description = "test",
        expirationDate = null
    )
    private val mockChallengeRepository = object : ChallengeRepositoryInterface {
        override suspend fun getChallenge(id: String): Challenge {
            return mockRepositoryChallenge
        }

        override fun getSectorChallenges(sector: String): Flow<List<Challenge>> {
            return flowOf(listOf(mockRepositoryChallenge))
        }

        override suspend fun getAuthor(challenge: Challenge): User {
            return User("test", "test", "test")
        }

        override fun getChallengePhoto(challenge: Challenge): String {
            return ""
        }

        override fun getPosts(userId: String): Flow<List<Challenge>> {
            return flowOf(listOf(mockRepositoryChallenge))
        }

        override suspend fun getClaims(challenge: Challenge): List<Claim> {
            return listOf(Claim("test", "test", "test", "test", LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12), 20L, 20L))
        }

        override suspend fun createChallenge(
            photo: LocalPicture,
            location: Location,
            difficulty: Challenge.Difficulty,
            expirationDate: LocalDateTime?,
            description: String?
        ): Challenge {
            return mockRepositoryChallenge
        }

        override suspend fun getChallenges(): List<Challenge> {
            return listOf(mockRepositoryChallenge)
        }
    }

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
                id = "Event $i",
                image = "",
                coordinates = LatLng(46.51958 + i * 0.01, 6.56398 + i * 0.01),
                expirationDate = LocalDateTime.of(LocalDateTime.now().year + 10, Month.MAY, 1, 19, 39, 12))
            )
        }
        mockTestChallengeDatabase.add(Marker(
            id = "Event 3",
            image = "test-url",
            coordinates = LatLng(46.5195, 6.5634),
            expirationDate = LocalDateTime.of(LocalDateTime.now().year + 10, Month.MAY, 1, 19, 39, 12))
        )
    }

    @Test
    fun testMapIsLoadingCorrectly() {
        composeTestRule.setContent {
            GoogleMapDisplay(
                Modifier.testTag("Maps"),
                cameraPosition = CameraPosition(epflCoordinates, 10f, 0f, 0f),
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
            .assertAny(hasTestTag("Marker image"))
    }
}
