package com.github.geohunt.app.data.repository

import android.app.Application
import android.location.Location
import com.github.geohunt.app.sensor.SharedLocationManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

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

    val location: LocationRepository = LocationRepository(
        SharedLocationManager(application.applicationContext)
    )
    val database = Firebase.database
    val storage = Firebase.storage

    val image = ImageRepository()
    val auth = AuthRepository()
    val user = UserRepository(image, auth)

    val challenges = ChallengeRepository(user, image, auth)
    val activeHunts = ActiveHuntsRepository(auth)
    val claims = ClaimRepository(auth, image)

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

        /**
         * Returns the singleton instance of [AppContainer] using the firebase emulator.
         *
         * This is pretty bad. but has to be done.
         */
        fun getEmulatedFirebaseInstance(
            application: Application
        ): AppContainer {
            try {
                Firebase.database.useEmulator("10.0.2.2", 9000)
                Firebase.storage.useEmulator("10.0.2.2", 9199)
            } catch(_: IllegalStateException) {

            }
            return getInstance(application)
        }
    }
}
