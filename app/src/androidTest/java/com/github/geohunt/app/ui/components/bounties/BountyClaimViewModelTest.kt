package com.github.geohunt.app.ui.components.bounties

import android.graphics.Bitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.bounties.BountiesRepository
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class BountyClaimViewModelTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val fineLocationRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val coarseLocationRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var repo: BountiesRepository
    private lateinit var bounty: Bounty

    private val mockAuth = MockAuthRepository()
    private val mockLocation = Location(33.047236, 86.971963)

    private lateinit var viewModel: BountyClaimViewModel

    @Before
    fun setup() {
        database = FirebaseEmulator.getEmulatedFirebase()
        storage = FirebaseEmulator.getEmulatedStorage()
        repo = BountiesRepository(
            MockUserRepository(mockAuth = mockAuth),
            MockAuthRepository(),
            ImageRepository(storage),
            database = database,
            storage = storage
        )

        composeTestRule.setContent {
            val bountyId = "bounty_name"
            val newViewModel: BountyClaimViewModel = viewModel(factory = BountyClaimViewModel.getFactory(bountyId))

            viewModel = newViewModel
        }

        runBlocking {
            mockAuth.loggedAs("1").run {
                bounty = repo.createBounty(
                    "bounty_name",
                    startingDate = LocalDateTime.now(),
                    expirationDate = LocalDateTime.now().plusDays(2),
                    location = mockLocation
                )
            }
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testThatTheInitialStateIsCorrect() = runBlockingTest {
        assertNull(viewModel.location.value)
        assertEquals(BountyClaimViewModel.State.AWAITING_CHALLENGE, viewModel.submittingState.value)
        assertNull(viewModel.photoState.value)
        assertNull(viewModel.challenge.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testSubmittingPhotoMakesSubmittingStateToAwaitingLocationPermission() = runBlockingTest {
        viewModel.start("test_challenge_id")
        viewModel.withPhoto({ Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) })

        assertEquals(BountyClaimViewModel.State.AWAITING_LOCATION_PERMISSION, viewModel.submittingState.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testResettingViewModelNullifiesValues() = runBlockingTest {
        viewModel.start("test_challenge_id")
        viewModel.withPhoto({ Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) })

        viewModel.reset()
        assertNull(viewModel.location.value)
        assertNull(viewModel.photoState.value)
        assertNull(viewModel.challenge.value)
        assertEquals(BountyClaimViewModel.State.AWAITING_CAMERA, viewModel.submittingState.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testStartLocationUpdateMakesSubmittingStateAwaitingLocation() = runBlockingTest {
        viewModel.start("test_challenge_id")
        viewModel.withPhoto({ Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) })

        viewModel.startLocationUpdate()
        assertEquals(BountyClaimViewModel.State.AWAITING_LOCATION, viewModel.submittingState.value)
    }
}
