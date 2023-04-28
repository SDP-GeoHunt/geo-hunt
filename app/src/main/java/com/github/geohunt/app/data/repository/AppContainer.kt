package com.github.geohunt.app.data.repository

import android.app.Application
import android.content.Context
import androidx.datastore.dataStore
import com.github.geohunt.app.data.settings.AppSettingsSerializer
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage



/**
 * Container for the application's dependency instances.
 *
 * This is a temporary solution until a proper dependency injection (DI) framework such as
 * [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is added to
 * the codebase.
 */
class AppContainer(application: Application) {
    val database = Firebase.database
    val storage = Firebase.storage

    val image = ImageRepository()
    // val auth = AuthRepository()
    // val user = UserRepository(image, auth)


    private val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)
    val appSettingsRepository = AppSettingsRepositoryImpl(application.dataStore)

    companion object {
        private var container: AppContainer? = null
        fun get(application: Application): AppContainer {
            if (container == null) {
                container = AppContainer(application)
            }
            return container as AppContainer
        }
    }
}