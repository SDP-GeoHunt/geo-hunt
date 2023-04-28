package com.github.geohunt.app.data.settings

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class AppSettingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun toOneWayMutableStateFlowTakesValue() {
        runTest {
            val cf = CompletableFuture<Void?>()
            val cfEmitted = CompletableFuture<Void?>()
            val flow = flow {
                cf.get() // blocking is intended
                emit(1)
                cfEmitted.complete(null)
            }.flowOn(Dispatchers.IO)
            val setter: suspend (Int) -> Unit = { }
            val testAppSetting = AppSetting(flow, 0, setter)

            var msf: MutableStateFlow<Int>? = null
            composeTestRule.setContent {
                msf = testAppSetting.toOneWayMutableStateFlow(rememberCoroutineScope())
            }
            assert(msf?.value == 0)
            cf.complete(null)
            cfEmitted.get(2, TimeUnit.SECONDS)
            assert(msf?.value == 1)
        }

    }
}