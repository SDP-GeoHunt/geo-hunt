package com.github.geohunt.app.ui.components.challengecreation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.database.api.Challenge
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class ChallengeSettingsTest {
    @get:Rule
    val testRule = createComposeRule()

    private fun setupComposable() {
        val difficulty = mutableStateOf(Challenge.Difficulty.MEDIUM)
        val date = mutableStateOf(LocalDate.of(2001, 7, 24))
        setupComposable(difficulty, date)
    }

    private fun setupComposable(selectedDifficulty: MutableState<Challenge.Difficulty>, selectedDate: MutableState<LocalDate?>) {
        testRule.setContent {
            ChallengeSettings(selectedDifficulty = selectedDifficulty, selectedDate = selectedDate)
        }
    }

    @Test
    fun settingsTextsAreDisplayed() {
        setupComposable()

        testRule.onNodeWithText("Difficulty", substring = true, useUnmergedTree = true).assertIsDisplayed()
        testRule.onNodeWithText("Date", substring = true, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun currentSelectionsAreDisplayed() {
        val difficulty = mutableStateOf(Challenge.Difficulty.MEDIUM)
        val date = mutableStateOf(LocalDate.of(2001, 7, 24))
        setupComposable(difficulty, date)

        testRule.onNodeWithText(difficulty.value.toString()).assertIsDisplayed()
        testRule.onNodeWithText(DateFormatUtils.formatDate(date.value)).assertIsDisplayed()

        difficulty.value = Challenge.Difficulty.HARD
        date.value = null

        testRule.onNodeWithText(difficulty.value.toString()).assertIsDisplayed()
        testRule.onNodeWithText("Never").assertIsDisplayed()
    }
}