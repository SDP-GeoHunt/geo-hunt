package com.github.geohunt.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.geohunt.app.R
import com.github.geohunt.app.mocks.*
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseChallenge
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.model.database.firebase.FirebaseUser
import com.github.geohunt.app.ui.components.challenge.ChallengeView
import com.github.geohunt.app.utility.findActivity
import com.google.android.gms.tasks.Tasks
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database : FirebaseDatabase
    private val TIMEOUT_TIME_MS = 5000L

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }

    @Before
    fun setup() {
        FirebaseEmulator.init()
    }

    @After
    fun cleanup() {}

    @Test
    fun testChallengeViewDisplayUserProperly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val profilePicture = createTestBitmap(context)
        var challenge2 : Challenge? = null
        var route = ""


        val author = MockUser(
            displayName = "John wick",
            score = 48723,
            profilePicture = InstantLazyRef("izufiozef", profilePicture)
        )

        val author2 = MockUser(
            displayName = "John Williams",
            score = 1248,
            profilePicture = InstantLazyRef("izufiozef", profilePicture)
        )

        val claim = object : Claim {
            override val id: String
                get() = "claim-ar4f165erf146a5c"
            override val challenge: LazyRef<Challenge>
                get() = MockLazyRef<Challenge>("cid") { TODO() }
            override val image: LazyRef<Bitmap>
                get() = MockLazyRef<Bitmap>("bitmap") { Tasks.forResult(createTestBitmap(context)) }
            override val user: LazyRef<User>
                get() = InstantLazyRef(author2.uid, author2)
            override val time: LocalDateTime
                get() = LocalDateTime.now()
            override val distance: Long
                get() = 62
            override val awardedPoints: Long
                get() = 100
            override val location: Location
                get() = Location()
        }

        val challenge = MockChallenge(
            author = MockLazyRef("user-f425zez6z4ef6z15f4") {
                Tasks.forResult(author)
            },
            thumbnail = MockLazyRef("img-ze5f16zaef1465") {
                Tasks.forResult(createTestBitmap(context))
            },
            claims = listOf(InstantLazyRef("claim", claim))
        )
        challenge2 = challenge

        // Sets the composeTestRule content
        composeTestRule.setContent {
            database = FirebaseDatabase(LocalContext.current.findActivity())

            ChallengeView(
                challenge = challenge,
                database = database,
                user = author,
                { route = it }) {
                route = "../"
            }
        }

        // Test stuff once loaded
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("profile-icon")
                .fetchSemanticsNodes()
                .size == 1
        }

        // Ensure click on challenge view image redirect to corresponding page
        composeTestRule.onNodeWithContentDescription("Challenge Image")
            .assertIsDisplayed()
            .performClick()
        assertThat(route, equalTo("img-ze5f16zaef1465"))

        composeTestRule.onNodeWithText("John wick")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Follow")
            .performScrollTo()
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithTag("btn-notification")
            .performScrollTo()
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithText("published just now")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("John Williams")
            .performScrollTo()
            .assertExists()

        composeTestRule.onNodeWithText("62m")
            .performScrollTo()
            .assertExists()
    }

    @Test
    fun testLikingButtonWorksProperly() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var route = ""

        val author = FirebaseUser(
            displayName = "John wick",
            score = 48723L,
            uid = "user-f425zez6z4ef6z15f4",
            hunts = listOf(),
            likes = listOf(),
            profilePicture = MockLazyRef("img-ze5f16zaef1465") {
                Tasks.forResult(createTestBitmap(context))
            },
            challenges = listOf(),
            follows = listOf(),
            numberOfFollowers = 0,
        )

        val challenge = FirebaseChallenge(
            author = MockLazyRef("user-f425zez6z4ef6z15f4") {
                Tasks.forResult(author)
            },
            claims = listOf(),
            thumbnail = MockLazyRef("img-ze5f16zaef1465") {
                Tasks.forResult(createTestBitmap(context))
            },
            correctLocation = Location(50.06638888888889, -5.714722222222222),
            cid = "cid",
            expirationDate = LocalDateTime.now().plusDays(1),
            publishedDate = LocalDateTime.now(),
            likes = listOf(),
            nbLikes = 0,
        )

        // Sets the composeTestRule content
        composeTestRule.setContent {
            database = FirebaseDatabase(LocalContext.current.findActivity())

            ChallengeView(
                challenge = challenge,
                database = database,
                user = author,
                { route = it }) {
                route = "../"
            }
        }

        // Check if the button for liking is loaded
        composeTestRule.waitUntil(TIMEOUT_TIME_MS) {
            composeTestRule.onAllNodesWithContentDescription("Likes")
                .fetchSemanticsNodes()
                .size == 1
        }

        // Find the button for liking and check if it exists
        composeTestRule.onNodeWithContentDescription("Likes")
            .assertExists()
            // Check if the number of likes is 0
            .assertTextEquals("0")
            // Check if the button has a click action and perform click
            .assertHasClickAction()
            .performClick()
            // Check if the number of likes has increased to 1
            .assertTextEquals("1")
            // Check again if the button has a click action and perform click
            .assertHasClickAction()
            .performClick()
            // Check if the number of likes has decreased to 0
            .assertTextEquals("0")
    }

    @Test
    fun testClickingOnLikeButtonWithPreviousLikeRemovesLike(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var route = ""

        val author = FirebaseUser(
            displayName = "John wick",
            score = 48723L,
            uid = "user-f425zez6z4ef6z15f4",
            hunts = listOf(),
            likes = listOf(),
            profilePicture = MockLazyRef("img-ze5f16zaef1465") {
                Tasks.forResult(createTestBitmap(context))
            },
            challenges = listOf(),
            follows = listOf(),
            numberOfFollowers = 0,
        )

        val challenge = FirebaseChallenge(
            author = MockLazyRef("user-f425zez6z4ef6z15f4") {
                Tasks.forResult(author)
            },
            claims = listOf(),
            thumbnail = MockLazyRef("img-ze5f16zaef1465") {
                Tasks.forResult(createTestBitmap(context))
            },
            correctLocation = Location(50.06638888888889, -5.714722222222222),
            cid = "cid",
            expirationDate = LocalDateTime.now().plusDays(1),
            publishedDate = LocalDateTime.now(),
            likes = listOf(),
            nbLikes = 5001,
        )

        // Sets the composeTestRule content
        composeTestRule.setContent {
            database = FirebaseDatabase(LocalContext.current.findActivity())

            // Add a like to the challenge
            database.insertUserLike(author.uid, challenge.cid)

            ChallengeView(
                challenge = challenge,
                database = database,
                user = author,
                { route = it }) {
                route = "../"
            }
        }

        // Check if the button for liking is loaded
        composeTestRule.waitUntil(TIMEOUT_TIME_MS) {
            composeTestRule.onAllNodesWithContentDescription("Likes")
                .fetchSemanticsNodes()
                .size == 1
        }


        // Check if the user has liked the challenge
        database.isUserLiked(author.uid, challenge.cid).fetch().addOnSuccessListener {
            assertThat(
                it, equalTo(true)
            )
        }

        // Find the button for liking and check if it exists
        composeTestRule.onNodeWithContentDescription("Likes")
            .assertExists()
            // Check if the initial number of likes is 5001
            .assertTextEquals("5001")
            // Check if the button has a click action and perform click
            .assertHasClickAction()
            .performClick()
            // Check if the number of likes has decreased to 5000
            .assertTextEquals("5000")

        // Check if the user has not liked the challenge
        database.isUserLiked(author.uid, challenge.cid).fetch().addOnSuccessListener {
            assertThat(
                it, equalTo(false)
            )
        }
    }
}