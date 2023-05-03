package com.github.geohunt.app.data.repository

import android.app.Application
import com.github.geohunt.app.domain.GetUserFeedUseCase
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.IllegalStateException

/**
 * Container for the application's dependency instances.
 *
 * This is a temporary solution until a proper dependency injection (DI) framework such as
 * [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is added to
 * the codebase.
 */
class AppContainer private constructor(application: Application) {
    init {
        try {
            Firebase.database.setPersistenceEnabled(true)
        } catch(_: DatabaseException) { /* This is already the case, why forcing it */ }
    }

    val database = Firebase.database
    val storage = Firebase.storage

    val auth = AuthRepository()

    val image = ImageRepository()
    val user = UserRepository(image, auth)
    val follow = FollowRepository(auth)

    val challenges = ChallengeRepository(user, image, auth)
    val activeHunts = ActiveHuntsRepository(auth)

    val feedUseCase = GetUserFeedUseCase(auth, challenges, follow)

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
