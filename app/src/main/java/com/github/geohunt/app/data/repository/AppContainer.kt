package com.github.geohunt.app.data.repository

import android.app.Application
import android.content.Context
import androidx.datastore.dataStore
import com.github.geohunt.app.data.repository.bounties.ActiveBountiesRepository
import com.github.geohunt.app.data.repository.bounties.BountiesRepository
import com.github.geohunt.app.data.settings.AppSettingsSerializer
import com.github.geohunt.app.domain.GetUserFeedUseCase
import com.github.geohunt.app.sensor.SharedLocationManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/**
 * Container for the application's dependency instances.
 *
 * This is a temporary solution until a proper dependency injection (DI) framework such as
 * [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is added to
 * the codebase.
 */
class AppContainer private constructor(dbInstance: FirebaseDatabase, storageInstance: FirebaseStorage, application: Application) {
    val location: LocationRepository = LocationRepository(
        SharedLocationManager(application.applicationContext)
    )

    val image = ImageRepository(storageInstance)
    val auth = AuthRepository()
    val user = UserRepository(image, auth, dbInstance)

    val score = ScoreRepository(dbInstance)
    val challenges = ChallengeRepository(user, image, auth, dbInstance)
    val activeHunts = ActiveHuntsRepository(auth, dbInstance)
    val claims = ClaimRepository(auth, image, dbInstance, score, activeHunts)
    val follow = FollowRepository(auth, dbInstance)

    val bounty = BountiesRepository(user, auth, image, dbInstance)
    val activeBounties = ActiveBountiesRepository(dbInstance, auth)

    val feedUseCase = GetUserFeedUseCase(auth, challenges, follow)

    // Settings
    private val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)
    val appSettingsRepository = AppSettingsRepositoryImpl(application.dataStore)

    // Profile visibilities
    val profileVisibilities = ProfileVisibilityRepository(dbInstance)


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
            { FirebaseDatabase.getInstance().apply {
                try {
                    this.setPersistenceEnabled(true)
                } catch(_: Exception) {}
            } },
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
            val dbInstance = FirebaseDatabase.getInstance("http://10.0.2.2:9000/?ns=geohunt-1-default-rtdb")
            val storageInstance = FirebaseStorage.getInstance("gs://geohunt-1.appspot.com")
            storageInstance.useEmulator("10.0.2.2", 9199)
            return getInstance({ dbInstance }, { storageInstance }, application)
        }

    }
}
