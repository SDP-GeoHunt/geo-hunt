package com.github.geohunt.app

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import coil.Coil
import coil.ImageLoader
import coil.request.CachePolicy
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.mocks.MockProfilePicture
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.leaderboard.Leaderboard
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import com.github.geohunt.app.utils.ImageIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LeaderboardTest {
    /**
     * The image idling resources. Need to be setup before and released after every test.
     */
    private lateinit var imageResources: ImageIdlingResource

    private val names = listOf(
        "John Smith",
        "Amrit Ayuba",
        "Teodósio Kajal",
        "Hjalmar Radoslav",
        "Arthit Dwain"
    )

    private val youIndex = 2

    /**
     * Creates a mock user with a random name in [[names]].
     */
    private fun mockUser(pos: Int): User {
        return object : User {
            override var displayName: String? = names[pos]
            override val uid: String = pos.toString()
            override val profilePicture: LazyRef<Bitmap> = MockProfilePicture
            override val profilePictureHash: Int = 1
            override val challenges: List<LazyRef<Challenge>> = listOf()
            override val activeHunts: List<LazyRef<Challenge>> = listOf()
            override val numberOfFollowers: Int = 10
            override val followList: List<LazyRef<User>> = emptyList()
            override val preferredLocale: String? = null
            override var score: Long = (1500 - pos * 100).toLong()
            override val isPOIUser: Boolean = false
            override var likes: List<LazyRef<Challenge>> = listOf()
        }
    }

    private val mockUsers = List(names.size) { i -> mockUser(i) }

    @get:Rule
    val testRule = createComposeRule()

    @Before
    fun setupMockLeaderboard() {
        imageResources = ImageIdlingResource()
        testRule.registerIdlingResource(imageResources)

        testRule.setContent {
            // Set up every [AsyncImage] to use this custom ImageLoader so that calls can be tracked
            Coil.setImageLoader(
                ImageLoader.Builder(getApplicationContext())
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .networkObserverEnabled(false)
                    .eventListener(imageResources)
                    .build()
            )

            GeoHuntTheme {
                Leaderboard(users = mockUsers, currentUser = mockUsers[youIndex])
            }
        }
    }

    @After
    fun releaseResources() {
        testRule.unregisterIdlingResource(imageResources)
    }

    @Test
    fun timeDropdownCorrectlySelectsOption() {
        testRule.onNode(hasText("All time") and hasClickAction()).performClick()
        testRule.onNode(hasText("Monthly") and hasClickAction()).performClick()

        // Check that the option was correctly selected
        testRule.onAllNodesWithText("All time").assertCountEquals(0)
        testRule.onAllNodesWithText("Monthly").assertCountEquals(1)
    }

    @Test
    fun usersAppearExactlyOnceInLeaderboard() {
        for ((i, user) in mockUsers.withIndex()) {
            // Check that every name is printed exactly once
            testRule.onAllNodesWithText(user.name).assertCountEquals(1)

            val siblings = testRule.onNodeWithText(user.name).onSiblings()

            // Check that the position is printed once
            val position = when(i) {
                in 0..2 -> "${i+1}."
                else -> (i + 1).toString()
            }

            siblings
                .filter(hasText(position))
                .assertCountEquals(1)

            // Check that the image is printed once
            siblings
                .filter(hasContentDescription("${user.name} profile picture"))
                .assertCountEquals(1)

            // Check that the score is printed once
            siblings
                .filter(hasText("${user.score.toSuffixedString()} pts"))
                .assertCountEquals(1)
        }
    }

    @Test
    fun topUserGetsFireIcon() {
        testRule.onNodeWithText(mockUsers[0].name)
            .onSiblings()
            .filterToOne(hasContentDescription("Fire !"))
            .assertIsDisplayed()
    }

    @Test
    fun youItemIsPresent() {
        val you = testRule.onNodeWithText("You", useUnmergedTree = true)

        you.assertIsDisplayed()
        you.onSiblings().filterToOne(hasTextExactly((youIndex + 1).toString())).assertIsDisplayed()
        you.onSiblings().filterToOne(hasTextExactly("${mockUsers[youIndex].score.toSuffixedString()} pts")).assertIsDisplayed()
    }
}