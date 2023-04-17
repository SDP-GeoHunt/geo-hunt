package com.github.geohunt.app.maps

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleMapViewTest {
    private val epflCoordinates = LatLng(46.519585, 6.5684919)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.setContent {
            GoogleMapView(
                Modifier.testTag("Maps"),
                cameraPositionState = CameraPositionState(CameraPosition(epflCoordinates, 15f, 0f, 0f)),
            )
        }
    }

    @Test
    fun testMapIsDisplayed() {
        composeTestRule.onNodeWithTag("Maps").assertExists()
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
