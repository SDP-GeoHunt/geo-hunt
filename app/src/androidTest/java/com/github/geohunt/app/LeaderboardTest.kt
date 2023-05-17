package com.github.geohunt.app

import androidx.compose.material.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import coil.Coil
import coil.ImageLoader
import coil.request.CachePolicy
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.mocks.mockUser
import com.github.geohunt.app.ui.components.leaderboard.Leaderboard
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardEntry
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardInformation
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

    private val mockUsers = List(names.size) { i -> mockUser(id = "dn$i", displayName = names[i]) }

    private val mockEntries = mockUsers.mapIndexed { index, user -> LeaderboardEntry(user.name, index.toLong()) { Text(user.name.reversed()) } }
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
                Leaderboard(LeaderboardInformation(mockEntries, youIndex))
            }
        }
    }

    @After
    fun releaseResources() {
        testRule.unregisterIdlingResource(imageResources)
    }

    //Removed from app for now
    /*@Test
    fun timeDropdownCorrectlySelectsOption() {
        testRule.onNode(hasText("All time") and hasClickAction()).performClick()
        testRule.onNode(hasText("Monthly") and hasClickAction()).performClick()

        // Check that the option was correctly selected
        testRule.onAllNodesWithText("All time").assertCountEquals(0)
        testRule.onAllNodesWithText("Monthly").assertCountEquals(1)
    }*/

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

            // Check that the score is printed once
            siblings
                .filter(hasText("${i.toLong().toSuffixedString()} pts"))
                .assertCountEquals(1)

            //If we are checking the current user this will be displayed twice
            if(i == youIndex) {
                testRule.onAllNodesWithText(user.name.reversed()).assertCountEquals(2)
            }
            else {
                // Check that the composable profile icon is called
                testRule.onNodeWithText(user.name.reversed()).assertIsDisplayed()
            }
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
        you.onSiblings().filterToOne(hasTextExactly("${youIndex.toLong().toSuffixedString()} pts")).assertIsDisplayed()
    }
}