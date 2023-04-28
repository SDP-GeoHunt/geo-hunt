package com.github.geohunt.app.data.repository

import android.app.Application
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
class AppContainer private constructor(application: Application) {
    init {
        Firebase.database.setPersistenceEnabled(true)
    }

    val database = Firebase.database
    val storage = Firebase.storage

    val image = ImageRepository()
    val auth = AuthRepository()
    val user = UserRepository(image, auth)

    companion object {
        private var container: AppContainer? = null

        /**
         * Returns the singleton instance of [AppContainer].
         */
        fun getInstance(application: Application): AppContainer {
            if (container == null) {
                container = AppContainer(application)
            }
            return container as AppContainer
        }
    }
}
