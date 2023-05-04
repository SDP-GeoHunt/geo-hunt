package com.github.geohunt.app.ui.components.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.model.Challenge
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ListDropdownMenuTest {
    @get:Rule
    val testRule = createComposeRule()

    //Utility values used for testing
    //Here I test using the Challenge.Difficulty enum but it could be any list / toString fct
    private val elements = Challenge.Difficulty.values().toList()
    private val toString: (Challenge.Difficulty) -> String = { it.toString() }

    private fun <T> setupComposable(state: MutableState<T>, elements: Collection<T>, toString: (T) -> String) {
        testRule.setContent {
            ListDropdownMenu(state = state.value, { state.value = it }, elements = elements, toString = toString)
        }
    }

    private fun setupComposable() {
        val mutableElement = mutableStateOf(Challenge.Difficulty.MEDIUM)
        setupComposable(mutableElement, elements, toString)
    }

    @Test
    fun menuDisplaysCurrentlySelectedElement() {
        val mutableElement = mutableStateOf(Challenge.Difficulty.MEDIUM)
        setupComposable(mutableElement, elements, toString)

        testRule.onNodeWithTag("dropdown_menu_text_field").assertTextEquals(toString(mutableElement.value))

        mutableElement.value = Challenge.Difficulty.HARD
        testRule.onNodeWithTag("dropdown_menu_text_field").assertTextEquals(toString(mutableElement.value))

        mutableElement.value = Challenge.Difficulty.EASY
        testRule.onNodeWithTag("dropdown_menu_text_field").assertTextEquals(toString(mutableElement.value))
    }

    @Test
    fun menuClickOpensBox() {
        setupComposable()
        testRule.onNodeWithTag("dropdown_menu_box").assertHasClickAction().performClick()

        testRule.onNodeWithTag("dropdown_menu_item_HARD").assertIsDisplayed()
    }

    @Test
    fun menuItemClickChangesValue() {
        val mutableElement = mutableStateOf(Challenge.Difficulty.MEDIUM)
        setupComposable(mutableElement, elements, toString)

        testRule.onNodeWithTag("dropdown_menu_box").assertHasClickAction().performClick()
        testRule.onNodeWithTag("dropdown_menu_item_HARD").assertHasClickAction().performClick()

        Assert.assertEquals(Challenge.Difficulty.HARD, mutableElement.value)
    }
}