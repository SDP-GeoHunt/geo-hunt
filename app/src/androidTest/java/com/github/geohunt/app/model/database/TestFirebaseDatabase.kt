package com.github.geohunt.app.model.database

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.findActivity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class TestFirebaseDatabase {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var database : FirebaseDatabase
    private val currentLocation = Location(48.858283, 2.294645)
    private val timeout : Long = 120

    @Before
    fun setup() {
        FirebaseEmulator.init()
        composeTestRule.setContent {
            database = FirebaseDatabase(LocalContext.current.findActivity())
        }
    }

    @After
    fun cleanup() {}

    @Test
    fun testFirebaseDatabaseUserHasUniqueReference() {
        val u1 = database.getUserById("id-unique")
        val u2 = database.getUserById("id-unique-2")

        assertThat(u1, not(sameInstance(u2)))
        assertThat(database.getUserById("id-unique"), sameInstance(u1))
        assertThat(database.getUserById("id-unique-2"), sameInstance(u2))
    }

    @Test
    fun testFirebaseDatabaseChallengeHasUniqueReference() {
        val c1 = database.getChallengeById("id-unique")
        val c2 = database.getChallengeById("id-unique-2")

        assertThat(c1, not(sameInstance(c2)))
        assertThat(database.getChallengeById("id-unique"), sameInstance(c1))
        assertThat(database.getChallengeById("id-unique-2"), sameInstance(c2))
    }

    @Test
    fun testFirebaseDatabaseCreateChallengeWorkUponSuccess() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap = createTestBitmap(context)
        val future = CompletableFuture<() -> Unit>()
        val difficulty = Challenge.Difficulty.MEDIUM

        val challengeTask = database.createChallenge(bitmap, currentLocation, null, difficulty)
            .addOnSuccessListener { challenge ->
                future.complete {
                    assertThat(challenge.correctLocation, equalTo(currentLocation))
                    assertThat(challenge.coarseLocation, equalTo(currentLocation.getCoarseLocation()))
                    assertThat(challenge.difficulty, equalTo(difficulty))
                }
            }
            .addOnFailureListener(future::completeExceptionally)

        future.get(timeout, TimeUnit.SECONDS)()
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }

    @Test
    fun databaseRetrievesCorrectlyUsers() {
        val cf = CompletableFuture<User>()
        database.getUserById("1").fetch().addOnCompleteListener {
            cf.complete(it.result)
        }.addOnFailureListener {
            cf.completeExceptionally(it)
        }
        val get = cf.get()
        assertThat(get.uid, equalTo("1"))
        assertThat(get.displayName, equalTo("Debug user"))
        assertThat(get.score, equalTo(123))
    }
}