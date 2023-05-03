package com.github.geohunt.app.model.database

import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule

class TestFirebaseChallengeRef {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var database : FirebaseDatabase
    private val currentLocation = Location(48.858283, 2.294645)
    private val timeout : Long = 120

    @Before
    fun setup() {
        FirebaseEmulator.init()
    }

    @After
    fun cleanup() {}

    /*
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testChallengeRefFetchWorkProperly() = runTest {
        composeTestRule.setContent {
            database = FirebaseDatabase(LocalContext.current.findActivity())
        }

        val challengeRef = database.getChallengeById("163f921c-NQWln8MlqnVhArUIdwE")
        assertThat(challengeRef.id, equalTo("163f921c-NQWln8MlqnVhArUIdwE"))

        // Allow the fetching to finish in at most 5 seconds
        assertFinishes(5.seconds) {
            val challenge = challengeRef.fetch().await()

            assertThat(challenge.correctLocation.latitude, closeTo(43.880433, 1e-5))
            assertThat(challenge.correctLocation.longitude, closeTo(-103.453748, 1e-5))
        }
    }*/
}