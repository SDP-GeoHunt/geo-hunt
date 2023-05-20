package com.github.geohunt.app.ui.components.bounties

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.bounties.BountiesRepository
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.utility.BitmapUtils
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
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

    private lateinit var listOfChallenges : List<Challenge>

    @Before
    fun setup() {
        database = FirebaseEmulator.getEmulatedFirebase()
        storage = FirebaseEmulator.getEmulatedStorage()
        repo = BountiesRepository(
            MockUserRepository(mockAuth = mockAuth),
            MockAuthRepository(),
            ImageRepository(storage),
            database = database
        )

        runBlocking {
            mockAuth.loggedAs("1").run {
                bounty = repo.createBounty(
                    "bounty_name",
                    startingDate = LocalDateTime.now(),
                    expirationDate = LocalDateTime.now().plusDays(200),
                    location = mockLocation
                )
            }
        }

        composeTestRule.setContent {
            val bountyId = "bounty_name"
            val newViewModel: BountyClaimViewModel = viewModel(factory = BountyClaimViewModel.getFactory(bountyId))

            viewModel = newViewModel
        }

        // Add some challenges to the bounty
        runBlocking {
            val file = withContext(Dispatchers.IO) {
                File.createTempFile("test", ".png").apply {
                    BitmapUtils.saveToFile(Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888),
                        this,
                        Bitmap.CompressFormat.PNG,
                        80)
                }
            }

            repo.getChallengeRepository(bounty).createChallenge(
                photo = LocalPicture(Uri.fromFile(file)),
                location = mockLocation
            )
            repo.getChallengeRepository(bounty).createChallenge(
                photo = LocalPicture(Uri.fromFile(file)),
                location = mockLocation
            )
            repo.getChallengeRepository(bounty).createChallenge(
                photo = LocalPicture(Uri.fromFile(file)),
                location = mockLocation
            )

            listOfChallenges = repo.getChallengeRepository(bounty).getChallenges()
        }
    }

    @Test
    fun testThatTheInitialStateIsCorrect() = runTest {
        assertNull(viewModel.location.value)
        assertEquals(BountyClaimViewModel.State.AWAITING_CHALLENGE, viewModel.submittingState.value)
        assertNull(viewModel.photoState.value)
        assertNull(viewModel.challenge.value)
    }

    @Test
    fun testSubmittingPhotoMakesSubmittingStateToAwaitingLocationPermission() = runTest {
        viewModel.start(listOfChallenges[0].id)
        viewModel.withPhoto({ Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) })

        assertEquals(BountyClaimViewModel.State.AWAITING_LOCATION_PERMISSION, viewModel.submittingState.value)
    }

    @Test
    fun testResettingViewModelNullifiesValues() = runTest {
        viewModel.start(listOfChallenges[0].id)
        viewModel.withPhoto({ Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) })

        viewModel.reset()
        assertNull(viewModel.location.value)
        assertNull(viewModel.photoState.value)
        assertNull(viewModel.challenge.value)
        assertEquals(BountyClaimViewModel.State.AWAITING_CAMERA, viewModel.submittingState.value)
    }

    @Test
    fun testStartLocationUpdateMakesSubmittingStateAwaitingLocation() = runTest {
        viewModel.start(listOfChallenges[0].id)
        viewModel.withPhoto({ Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) })

        viewModel.startLocationUpdate()
        assertEquals(BountyClaimViewModel.State.AWAITING_LOCATION, viewModel.submittingState.value)
    }

    @Test
    fun testSubmittingClaimWithUninitializedLocationThrowsErrorWhenClaiming() = runTest {
        var file: File

        runBlocking {
            file = withContext(Dispatchers.IO) {
                File.createTempFile("test", ".png").apply {
                    BitmapUtils.saveToFile(Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888),
                        this,
                        Bitmap.CompressFormat.PNG,
                        80)
                }
            }
        }

        viewModel.start(listOfChallenges[0].id)
        assertEquals(BountyClaimViewModel.State.AWAITING_CAMERA, viewModel.submittingState.value)

        viewModel.withPhoto(file)
        assertEquals(BountyClaimViewModel.State.AWAITING_LOCATION_PERMISSION, viewModel.submittingState.value)

        viewModel.startLocationUpdate()

        val fileFactory: (String) -> File = { File(it) }
        assertThrows(IllegalArgumentException::class.java) {
            viewModel.claim(fileFactory)
        }
    }

    @Test
    fun testSubmittingClaimFinishesClaimingCorrectly() = runTest {
        viewModel.start(listOfChallenges[0].id)
        assertEquals(BountyClaimViewModel.State.AWAITING_CAMERA, viewModel.submittingState.value)

        val challenge = listOfChallenges[0]
        viewModel.injectChallenge(challenge)

        viewModel.injectPhoto(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        assertEquals(BountyClaimViewModel.State.AWAITING_LOCATION_PERMISSION, viewModel.submittingState.value)

        viewModel.injectLocationUpdate(mockLocation)
        assertEquals(BountyClaimViewModel.State.READY_TO_CLAIM, viewModel.submittingState.value)

        val fileFactory: (String) -> File = { File(it) }
        viewModel.claim(fileFactory)
    }
}
