package com.github.geohunt.app.ui.screens.bounty_team_select

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class TeamCreatorTest {
    @get:Rule
    val c = createComposeRule()

    @Test
    fun areAllElementsDisabledIfDisabled() {
        c.setContent {
            TeamCreator(createTeam = { }, disabled = true)
        }
        c.onNodeWithTag("team-creator-button").assertIsNotEnabled()
        c.onNodeWithTag("team-creator-field").assertIsNotEnabled()
    }

    @Test
    fun containsTheCreateTeamText() {
        c.setContent {
            TeamCreator(createTeam = {}, disabled = true)
        }
        c.onNodeWithText("Create team").assertIsDisplayed()
    }

    @Test
    fun buttonIsDisabledIfEmptyText() {
        c.setContent {
            TeamCreator(createTeam = {}, disabled = false)
        }
        c.onNodeWithTag("team-creator-button").assertIsNotEnabled()
    }

    @Test
    fun buttonIsEnabledIfNotEmptyText() {
        c.setContent {
            TeamCreator(createTeam = {}, disabled = false)
        }
        c.onNodeWithTag("team-creator-field").performTextInput("hello")
        c.onNodeWithTag("team-creator-button").assertIsEnabled()
    }

    @Test
    fun clickingOnButtonTriggersCallback() {
        val cf = CompletableFuture<String>()
        c.setContent {
            TeamCreator(createTeam = { cf.complete(it) }, disabled = false)
        }
        c.onNodeWithTag("team-creator-field").performTextInput("hello")
        c.onNodeWithTag("team-creator-button").performClick()
        assert(cf.get(2, TimeUnit.SECONDS) == "hello")
    }
}