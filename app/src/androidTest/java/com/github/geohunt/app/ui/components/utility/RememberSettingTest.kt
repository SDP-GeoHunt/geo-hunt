package com.github.geohunt.app.ui.components.utility

import androidx.compose.runtime.MutableState
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.settings.Setting
import com.github.geohunt.app.ui.rememberSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class RememberSettingTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun containsDefaultValue() {
        val setting = Setting(flowOf(), 1) {}
        var a: MutableState<Int>? = null
        compose.setContent {
            a = rememberSetting(s = setting)
        }
        assert(a?.value == 1)
    }

    @Test
    fun updatesWhenFlowEmits() {
        val cf = CompletableFuture<Void?>()
        val cfUpdate = CompletableFuture<Void?>()
        val flow = flow {
            emit(1)
            cf.get()
            emit(2)
            cfUpdate.complete(null)
        }.flowOn(Dispatchers.IO)

        val setting = Setting(flow, 1) {}
        var a: MutableState<Int>? = null
        compose.setContent {
            a = rememberSetting(s = setting)
        }
        assert(a?.value == 1)
        cf.complete(null)
        cfUpdate.get(2, TimeUnit.SECONDS)
        assert(a?.value == 2)
    }

    @Test
    fun callsSetterProperly() {
        val cf = CompletableFuture<Int>()
        val setting = Setting(flowOf(1), 1) { cf.complete(it) }
        compose.setContent {
            val k = rememberSetting(s = setting, false)
            k.value = 124
        }
        assert(cf.get(2, TimeUnit.SECONDS) == 124)
    }
}