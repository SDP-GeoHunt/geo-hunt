package com.github.geohunt.app.data.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.mocks.MockActiveHuntRepository
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockScoreRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.utility.BitmapUtils
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime


@Suppress("DEPRECATION")
@OptIn(ExperimentalCoroutinesApi::class)
class ClaimRepositoryTest {
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val ioDispatcher = UnconfinedTestDispatcher()
    private val auth = MockAuthRepository()
    private val currentUserId = auth.getCurrentUser().id

    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
        storage = FirebaseEmulator.getEmulatedStorage()
    }

    @After
    fun clearEmulator() {
    }

    private fun claimRepository(currentUser: AuthRepositoryInterface = auth): ClaimRepositoryInterface {
        val imageRepository = ImageRepository(storage)
        val mockScoreRepository = MockScoreRepository()
        val mockActiveHuntRepository = MockActiveHuntRepository(
            listOf("1")
        )
        return ClaimRepository(currentUser, imageRepository, database, mockScoreRepository, mockActiveHuntRepository, ioDispatcher)
    }

    @Test
    fun testPipeline() = runTest {
        withContext(Dispatchers.IO) {
            val repository = claimRepository()
            val challenge = Challenge(
                "1", "1", "", Location(0.0, 0.0),
                LocalDateTime.now(),
                null,
                Challenge.Difficulty.MEDIUM,
                null
            )

            val claim = repository.claimChallenge(
                createFile(),
                Location(0.0, 0.0),
                challenge
            )

            assertThat(claim.distance, lessThan(2))
            assertThat(claim.parentChallengeId, equalTo("1"))

            val claim2 = repository.getChallengeClaims(challenge)
            assertThat(claim2.map { it.id }, containsInAnyOrder(claim.id))
        }
    }

    private suspend fun createFile() : LocalPicture {
        val file = createTempFile("_IMG", ".jpg")

        BitmapUtils.saveToFile(
            Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888),
            file,
            Bitmap.CompressFormat.JPEG,
            100
        )

        return LocalPicture(Uri.fromFile(file))
    }
}