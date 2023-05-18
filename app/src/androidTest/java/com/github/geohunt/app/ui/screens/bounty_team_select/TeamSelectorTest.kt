package com.github.geohunt.app.ui.screens.bounty_team_select

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.mocks.MockUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class TeamSelectorTest {
    @get:Rule
    val c = createComposeRule()

    @Test
    fun showsTheName() {
        c.setContent {
            TeamSelector(name = "hello")
        }
        c.onNodeWithText("hello").assertIsDisplayed()
    }

    @Test
    fun showsLeaveIconIfInside() {
        c.setContent {
            TeamSelector(name = "hello", isUserInside = true)
        }
        c.onNodeWithTag("leave-btn", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun showsJoinIconIfNotInside() {
        c.setContent {
            TeamSelector(name = "hello")
        }
        c.onNodeWithTag("join-btn", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun showsDeleteButtonIfEnabled() {
        c.setContent {
            TeamSelector(name = "hello", canDelete = true)
        }
        c.onNodeWithTag("delete-btn", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun clickingOnDeleteButtonTriggersCallback() {
        val cf = CompletableFuture<Void?>()
        c.setContent {
            TeamSelector(name = "hello", canDelete = true, onDelete = { cf.complete(null) })
        }
        c.onNodeWithTag("delete-btn", useUnmergedTree = true).performClick()
        cf.get(2, TimeUnit.SECONDS)
    }

    @Test
    fun clickingOnJoinTriggersAction() {
        val cf = CompletableFuture<Void?>()
        c.setContent {
            TeamSelector(name = "hello", onAction = { cf.complete(null) })
        }
        c.onNodeWithTag("join-btn", useUnmergedTree = true).performClick()
        cf.get(2, TimeUnit.SECONDS)
    }

    @Test
    fun clickingOnLeaveTriggersAction() {
        val cf = CompletableFuture<Void?>()
        c.setContent {
            TeamSelector(name = "hello", onAction = { cf.complete(null) }, isUserInside = true)
        }
        c.onNodeWithTag("leave-btn", useUnmergedTree = true).performClick()
        cf.get(2, TimeUnit.SECONDS)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun showsTheCorrectNumberOfUsers() = runTest {
        val users = listOf(
            MockUserRepository().getUser("1"),
            MockUserRepository().getUser("2"),
            MockUserRepository().getUser("3"),
        )
        c.setContent {
            TeamSelector(name = "hello", users = users)
        }
        c.onAllNodesWithTag("user-entry").assertCountEquals(users.size)
    }
}