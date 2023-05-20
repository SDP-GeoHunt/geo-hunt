package com.github.geohunt.app.ui.components.buttons

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class BackButtonTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun backButtonCallsOnClickUponClicking() {
        var clicked = false
        testRule.setContent { BackButton(onClick = { clicked = true }) }

        testRule.onNodeWithTag("backButton").performClick()
        assert(clicked)
    }
}