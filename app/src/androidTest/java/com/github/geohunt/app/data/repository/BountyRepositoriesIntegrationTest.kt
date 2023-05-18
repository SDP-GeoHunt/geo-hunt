package com.github.geohunt.app.data.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.github.geohunt.app.data.local.LocalPicture
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class BountyRepositoriesIntegrationTest {
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var repo: BountiesRepository
    private lateinit var bounty: Bounty

    val mockAuth = MockAuthRepository()
    val mockLocation = Location(33.047236, 86.971963)

    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
        storage = FirebaseEmulator.getEmulatedStorage()
        repo = BountiesRepository(
            MockUserRepository(mockAuth = mockAuth),
            MockAuthRepository(),
            ImageRepository(storage),
            database = database,
            storage = storage
        )

        // Create a new bounty
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

    /// The admin (user 1)
    //    1. Create a challenge
    //  The user 2
    //    1. Create and join a team
    //    2. Claim this challenge
    //    3. Fetches the score
    @Test
    fun userStory1() = runTest {
        val file = withContext(Dispatchers.IO) {
            File.createTempFile("test", ".png").apply {
                BitmapUtils.saveToFile(Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888),
                    this,
                    Bitmap.CompressFormat.PNG,
                    80)
            }
        }

        var cid : String = ""
        var challenge: Challenge
        mockAuth.loggedAs("1").run {
            challenge = repo.getChallengeRepository(bounty)
                .createChallenge(
                    photo = LocalPicture(Uri.fromFile(file)),
                    location = mockLocation
                )
            cid = challenge.id
        }

        // Check the challenge exists
        assertThat(repo.getChallengeRepository(bounty).getChallenges().map { it.id },
            equalTo(listOf(cid)))

        // Secondly as the second user I want to join
        // the challenge
        var tid = ""
        mockAuth.loggedAs("2").run {
            @Suppress("DEPRECATION")
            tid = repo.getTeamRepository(bounty)
                .createTeam("name", mockAuth.getCurrentUser())
                .teamId

            // When creating a team, become part of the team
            val team = repo.getTeamRepository(bounty).getUserTeam().first()

            assert(team != null)

            assertThat(team!!.teamId, equalTo(tid))
            assertThat(team.leaderUid, equalTo("2"))
            assertThat(team.membersUid, equalTo(listOf("2")))

            // Finally claim the challenge
            repo.getClaimRepository(bounty).claimChallenge(
                photo = LocalPicture(Uri.fromFile(file)),
                location = mockLocation,
                challenge = challenge
            )

            assertThat(repo.getClaimRepository(bounty).getClaimsOf(team).size, equalTo(1))
            assertThat(repo.getTeamRepository(bounty).getUserTeam().first()?.score, equalTo(5000))
        }

    }
}