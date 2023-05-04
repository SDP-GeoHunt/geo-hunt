package com.github.geohunt.app.data.repository

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.datastore.dataStore
import com.github.geohunt.app.data.settings.AppSettingsSerializer
import com.github.geohunt.app.data.settings.Theme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AppSettingsRepositoryImplTest {
    @get:Rule
    val r = createAndroidComposeRule<ComponentActivity>()

    private val Context.dataStore by dataStore("test-settings.json", AppSettingsSerializer)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun settingThemeUpdatesState() {
        var repository: AppSettingsRepository? = null
        r.setContent {
            val dS = LocalContext.current.dataStore
            repository = AppSettingsRepositoryImpl(dS)
        }

        runTest {
            repository!!.themeSetting.setter(Theme.LIGHT)
            val flow = repository!!.themeSetting.flow
            val s = flow.first()
            assert(s == Theme.LIGHT)
            repository!!.themeSetting.setter(Theme.DARK)
            assert(flow.first() == Theme.DARK)
        }
    }
}