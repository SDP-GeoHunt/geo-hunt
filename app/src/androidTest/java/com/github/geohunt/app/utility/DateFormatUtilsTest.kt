package com.github.geohunt.app.utility

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class DateFormatUtilsTest {

    @get:Rule
    val composableTestRule = createComposeRule()

    private fun getAgo(value: Duration) : LocalDateTime {
        return LocalDateTime.now().minus(value.toLong(DurationUnit.MILLISECONDS), ChronoUnit.MILLIS)
    }

    private fun fetch(value: Duration) : String {
        val future = CompletableFuture<String>()
        composableTestRule.setContent {
            future.complete(
               DateFormatUtils.getElapsedTimeString(dateTime = getAgo(value), prefixStringId = R.string.claimed_format)
            )
        }
        return future.join()
    }

    @Test
    fun testGetElapsedTimeString10S() {
        assertThat(fetch(10.seconds), equalTo("claimed just now"))
    }

    @Test
    fun testGetElapsedTimeString60S() {
        assertThat(fetch(60.seconds), equalTo("claimed just now"))
    }

    @Test
    fun testGetElapsedTimeString5m() {
        assertThat(fetch(5.minutes), equalTo("claimed 5 minutes ago"))
    }

    @Test
    fun testGetElapsedTimeString7m() {
        assertThat(fetch(7.minutes), equalTo("claimed 5 minutes ago"))
    }

    @Test
    fun testGetElapsedTimeString28m() {
        assertThat(fetch(28.minutes), equalTo("claimed 25 minutes ago"))
    }

    @Test
    fun testGetElapsedTimeString64m() {
        assertThat(fetch(64.minutes), equalTo("claimed 1 hour ago"))
    }

    @Test
    fun testGetElapsedTimeString142m() {
        assertThat(fetch(142.minutes), equalTo("claimed 2 hours ago"))
    }

    @Test
    fun testGetElapsedTimeString1d() {
        assertThat(fetch(1.days), equalTo("claimed 1 day ago"))
    }

    @Test
    fun testGetElapsedTimeString2d() {
        assertThat(fetch(2.days), equalTo("claimed 2 days ago"))
    }

    @Test
    fun testGetElapsedTimeString30d() {
        assertThat(fetch(30.days), equalTo("claimed 1 month ago"))
    }

    @Test
    fun testGetElapsedTimeString60d() {
        assertThat(fetch(60.days), equalTo("claimed 2 months ago"))
    }

    @Test
    fun testGetElapsedTimeString190d() {
        assertThat(fetch(190.days), equalTo("claimed 1 year ago"))
    }

    @Test
    fun testGetElapsedTimeString640d() {
        assertThat(fetch(640.days), equalTo("claimed 2 years ago"))
    }
}