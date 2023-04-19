package com.github.geohunt.app.maps

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleMapViewTest {
    private val epflCoordinates = LatLng(46.519585, 6.5684919)
    private lateinit var device: UiDevice
    private val LAUNCH_TIMEOUT = 5000L
    private val TEST_PACKAGE = "com.github.geohunt.app.maps.androidTest.maps.GoogleMapViewTest"

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        device = UiDevice.getInstance(getInstrumentation())

        val launcherPackage: String = device.launcherPackageName
        assertThat(launcherPackage, notNullValue())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT
        )

        composeTestRule.setContent {
            GoogleMapView(
                Modifier.testTag("Maps"),
                cameraPositionState = CameraPositionState(CameraPosition(epflCoordinates, 15f, 0f, 0f)),
            )
        }

        device.wait(
            Until.hasObject(By.pkg(TEST_PACKAGE).depth(0)),
            LAUNCH_TIMEOUT
        )
    }

    @Test
    fun testMapIsDisplayed() {
        composeTestRule.onNodeWithTag("Maps").assertExists()
    }

    @Test
    fun testClickOnAllThreeMockMarkers() {
        // Click on the 1st marker
        device.findObject(UiSelector().descriptionContains("Event 1")).click()

        // Click on the 2nd marker
        device.findObject(UiSelector().descriptionContains("Event 2")).click()

        // Click on the 3rd marker
        device.findObject(UiSelector().descriptionContains("Event 3")).click()
    }
}