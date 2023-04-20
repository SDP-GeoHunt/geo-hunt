package com.github.geohunt.app.ui.components.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture

class FlagLongButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun clickingOnButtonTriggersCallback() {
        val cf = CompletableFuture<Void?>()
        composeRule.setContent {
            FlatLongButton(
                icon = Icons.Default.Home,
                text = "Home",
                onClick = { cf.complete(null) },
                modifier = Modifier.testTag("btn")
            )
        }
        composeRule.onNodeWithTag("btn").performClick()
        assert(cf.isDone)
    }

    @Test
    fun containsText() {
        composeRule.setContent {
            FlatLongButton(icon = Icons.Default.Home, text = "Test text", onClick = { })
        }
        composeRule.onNodeWithText("Test text").assertExists()
    }

    @Test
    fun containsContentDescriptionForIcon() {
        composeRule.setContent {
            FlatLongButton(icon = Icons.Default.Home, text = "Description", onClick = { })
        }
        composeRule.onNodeWithContentDescription("Description").assertExists()
    }
}