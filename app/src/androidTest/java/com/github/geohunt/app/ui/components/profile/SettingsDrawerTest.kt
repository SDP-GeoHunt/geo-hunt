package com.github.geohunt.app.ui.components.profile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class SettingsDrawerTest {
    @get:Rule
    val c = createComposeRule()
    
    @Test
    fun buttonsTriggersCorrectOpens() {
        val cfProfileEdit = CompletableFuture<Void?>()
        val cfLeaderboard = CompletableFuture<Void?>()
        val cfLogout = CompletableFuture<Void?>()
        val numberOfCloses = AtomicInteger(0)
        c.setContent {
            SettingsDrawer(
                openProfileEdit = { cfProfileEdit.complete(null) },
                openLeaderboard = { cfLeaderboard.complete(null) },
                onLogout = { cfLogout.complete(null) }) {
                numberOfCloses.incrementAndGet()
            }
        }
        clickAndTest("btn-open-profile-edit", cfProfileEdit)
        clickAndTest("btn-open-leaderboard", cfLeaderboard)

        // Double click for log off
        c.onNodeWithTag("btn-log-off").performClick()
        assert(!cfLogout.isDone)
        clickAndTest("btn-log-off", cfLogout)
        assert(numberOfCloses.get() == 3)
    }

    private fun <T> clickAndTest(testTag: String, cf: CompletableFuture<T>) {
        c.onNodeWithTag(testTag).performClick()
        assert(cf.isDone)
        assert(!cf.isCompletedExceptionally)
    }
}