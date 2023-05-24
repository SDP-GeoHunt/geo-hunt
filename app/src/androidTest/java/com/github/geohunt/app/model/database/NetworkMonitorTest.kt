package com.github.geohunt.app.model.database

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.data.network.NetworkMonitor
import com.github.geohunt.app.utils.assertFinishes
import com.github.geohunt.app.utils.assertTimesOut
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class NetworkMonitorTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: FirebaseDatabase
    private lateinit var monitor: NetworkMonitor

    private val STATUS_CHANGE_TIMEOUT: Duration = 10.seconds

    private suspend fun assertOnline(timeout: Duration) =
        assertFinishes(timeout) {
            monitor.isConnected.first { it }
        }

    private suspend fun assertOffline(timeout: Duration) =
        assertTimesOut(timeout) {
            monitor.isConnected.collect { isConnected -> assert(!isConnected) }
        }

    @Before
    fun setup() {
        // Note that mocking the database is too hard
        // Because Kotlin's extension getters are impossible to mock
        // and in particular Query.snapshots (used by the monitor) is not mockable
        database = FirebaseEmulator.getEmulatedFirebase()
        monitor = NetworkMonitor(database, ioDispatcher = UnconfinedTestDispatcher())
    }

    @After
    fun cleanUp() {
        // Restore Firebase connectivity state
        database.goOnline()
    }

    @Test
    fun isInitiallyOnline() = runTest {
        assertOnline(STATUS_CHANGE_TIMEOUT)
    }

    @Test
    fun goingOfflineDoesNotEmitOnline() = runTest {
        database.goOffline()

        // Check that there is no "online" status emitted in the next 5 seconds
        assertOffline(STATUS_CHANGE_TIMEOUT)
    }

    @Test
    fun goingBackOnlineChangesConnectivityState() = runTest {
        database.goOffline()
        database.goOnline()

        // Check that it eventually reaches online connectivity
        assertOnline(STATUS_CHANGE_TIMEOUT * 2)
    }
}
