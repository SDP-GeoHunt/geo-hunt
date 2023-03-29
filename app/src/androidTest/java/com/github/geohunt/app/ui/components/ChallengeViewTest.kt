package com.github.geohunt.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.widget.BaseExpandableListAdapter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.geohunt.app.R
import com.github.geohunt.app.mocks.*
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseChallenge
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.ui.components.navigation.NavigationBar
import com.github.geohunt.app.ui.components.navigation.NavigationController
import com.github.geohunt.app.utility.findActivity
import com.google.android.gms.tasks.Tasks
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database : FirebaseDatabase

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }

    @Before
    fun setup() {
        FirebaseEmulator.init()
    }

    @Test
    fun testChallengeViewDisplayUserProperly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val profilePicture = createTestBitmap(context)
        var challenge2 : Challenge? = null

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
                user = author,
                database = database,
                {},
                displayImage = {})
        }

        // Test stuff once loaded
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("profile-icon")
                .fetchSemanticsNodes()
                .size == 1
        }

        // Ensure click on challenge view image redirect to corresponding page

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
    fun testChallengeViewDisplayChallengeProperly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val future = CompletableFuture<String>()

        val author = MockUser(
            displayName = "John wick",
            score = 48723
        )

        val challenge = MockChallenge(
            author = MockLazyRef("user-f425zez6z4ef6z15f4") {
                Tasks.forResult(author)
            },
            thumbnail = MockLazyRef("img-ze5f16zaef1465") {
                Tasks.forResult(createTestBitmap(context))
            }
        )

        // Sets the composeTestRule content
        composeTestRule.setContent {
            database = FirebaseDatabase(LocalContext.current.findActivity())

            ChallengeView(
                challenge = challenge,
                user = author,
                database = database,
                {},
                displayImage = { iid ->
                    future.complete(iid)
                })
        }

        // Test stuff once loaded
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithContentDescription("Challenge Image")
                .fetchSemanticsNodes()
                .size == 1
        }

        // Ensure click on challenge view image redirect to corresponding page
        composeTestRule.onNodeWithContentDescription("Challenge Image")
            .assertHasClickAction()
            .performClick()

        assertThat(future.join(),
            equalTo("img-ze5f16zaef1465"))
    }

    @Test
    fun testLikingButtonWorksProperly() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val future = CompletableFuture<String>()

        val author = MockUser(
            displayName = "John wick",
            score = 48723,
            uid = "user-f425zez6z4ef6z15f4",
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
            likes = 0,
        )

        // Sets the composeTestRule content
        composeTestRule.setContent {
            database = FirebaseDatabase(LocalContext.current.findActivity())

            ChallengeView(
                challenge = challenge,
                user = author,
                //database
                database  = database, //object : BaseMockDatabase() {},
                {},
                displayImage = { iid ->
                    future.complete(iid)
                })
        }

        // Test stuff once loaded
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithContentDescription("Challenge Image")
                .fetchSemanticsNodes()
                .size == 1
        }

        //////Remove later
        // Ensure click on challenge view image redirect to corresponding page
        composeTestRule.onNodeWithContentDescription("Challenge Image")
            .assertHasClickAction()
            .performClick()
        //////////////////////////////

        //composeTestRule.waitUntil(10000) {
        //    composeTestRule.onAllNodesWithTag("like_button")
        //        .fetchSemanticsNodes()
        //        .size == 1
        //}

        // Check if the initial number of likes is 0
        composeTestRule.onNodeWithTag("like_count")
            .assertTextEquals("0")
        //onNodeWithText("0")
            .assertExists()

        // Perform click that will add the like
        composeTestRule.onNodeWithContentDescription("Likes")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        // Check if the number of likes has increased to 1
        composeTestRule.onNodeWithTag("like_count")
            //.onNodeWithText("1")
            .assertTextEquals("1")
            .assertExists()

        // Perform click that will remove the like
        composeTestRule.onNodeWithContentDescription("Likes")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        // Check if the number of likes has decreased to 0
        composeTestRule.onNodeWithText("0")
            .assertExists()

        // Click on the like button
       // composeTestRule.onAllNodesWithTag("like_button")
       //     .assertCountEquals(1)
       //     .assertAny(hasClickAction())
            //.assertExists()
            //.performClick()

        // Check if the number of likes has increased
//        composeTestRule.onNodeWithText("0") //TODO: Change to 1
//            .assertExists()


        assertThat(future.join(),
            equalTo("img-ze5f16zaef1465"))
    }
}