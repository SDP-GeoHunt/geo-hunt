package com.github.geohunt.app.ui.components.utils

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

/**
 * Tests for [SkeletonLoading]-based components.
 */
@OptIn(ExperimentalTestApi::class)
class SkeletonTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun skeletonLoadingShowsSkeletonWhenValueIsNull() {
        testRule.setContent {
            SkeletonLoading(value = null, width = 0.dp, height = 0.dp) {}
        }

        testRule.onNodeWithTag("skeleton").assertExists()
    }

    @Test
    fun skeletonLoadingShowsContentWhenLoaded() {
        val string = "Hello world !"

        testRule.setContent {
            SkeletonLoading(value = string, width = 80.dp, height = 18.dp) {
                Text(it)
            }
        }
        
        testRule.waitUntilAtLeastOneExists(hasTextExactly(string), timeoutMillis = 2000L)
    }
}