package com.github.geohunt.app.data.repository

import android.app.Application
import android.util.Log
import com.github.geohunt.app.domain.GetUserFeedUseCase
import com.github.geohunt.app.sensor.SharedLocationManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.lang.IllegalStateException

/**
 * Container for the application's dependency instances.
 *
 * This is a temporary solution until a proper dependency injection (DI) framework such as
 * [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is added to
 * the codebase.
 */
class AppContainer private constructor(dbInstance: FirebaseDatabase, storageInstance: FirebaseStorage, application: Application) {
    val database = Firebase.database
    val location: LocationRepository = LocationRepository(
        SharedLocationManager(application.applicationContext)
    )

    val image = ImageRepository(storageInstance)
    val auth = AuthRepository()
    val user = UserRepository(image, auth, dbInstance)

    val challenges = ChallengeRepository(user, image, auth, dbInstance)
    val activeHunts = ActiveHuntsRepository(auth, dbInstance)
    val claims = ClaimRepository(auth, image, dbInstance)
    val follow = FollowRepository(auth, dbInstance)

    val feedUseCase = GetUserFeedUseCase(auth, challenges, follow)

    companion object {
        private var container: AppContainer? = null

        private fun getInstance(dbInstance: () -> FirebaseDatabase, storage: () -> FirebaseStorage, application: Application) : AppContainer {
            if (container == null) {
                container = AppContainer(dbInstance(), storage(), application)
            }
            return container as AppContainer
        }

        /**
         * Get the instance for the current AppContainer
         */
        fun getInstance(application: Application): AppContainer = getInstance(
            { FirebaseDatabase.getInstance().apply { this.setPersistenceEnabled(true) } },
            { FirebaseStorage.getInstance() },
            application
        )

        /**
         * Returns the singleton instance of [AppContainer] using the firebase emulator.
         *
         * This is pretty bad. but has to be done.
         */
        fun getEmulatedFirebaseInstance(
            application: Application
        ): AppContainer {
            val dbInstance = FirebaseDatabase.getInstance()
            val storageInstance = FirebaseStorage.getInstance()
            try {
                dbInstance.useEmulator("10.0.2.2", 9000)
                storageInstance.useEmulator("10.0.2.2", 9199)
            } catch(e: IllegalStateException) {
                Log.w("GeoHuntDebug", "Failed to use the emulator: $e")
            }
            return getInstance({ dbInstance }, { storageInstance }, application)
        }

    }
}
