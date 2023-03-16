package com.github.geohunt.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleMapsActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<GoogleMapsActivity>()

    @get:Rule
    val permissionRuleFineLocation: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val permissionRuleCoarseLocation: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    @Test
    fun testMapDisplayed() {
        onView(withId(R.id.map_container_view)).check(matches(isDisplayed()))
    }

    @Test
    fun testMapIsNotNull() {
        onView(withId(R.id.map_container_view)).check(matches(notNullValue()))
    }

    @Test
    fun testMarkerClickOnEvent1WorksCorrectly() {
        val device: UiDevice = UiDevice.getInstance(getInstrumentation())
        val marker: UiObject = device.findObject(UiSelector().descriptionContains("Event 1"))
        marker.click()
    }

    @Test
    fun testMarkerClickOnEvent2WorksCorrectly() {
        val device: UiDevice = UiDevice.getInstance(getInstrumentation())
        val marker: UiObject = device.findObject(UiSelector().descriptionContains("Event 2"))
        marker.click()
    }

    @Test
    fun testMarkerClickOnEvent3WorksCorrectly() {
        val device: UiDevice = UiDevice.getInstance(getInstrumentation())
        val marker: UiObject = device.findObject(UiSelector().descriptionContains("Event 3"))
        marker.click()
    }
}
