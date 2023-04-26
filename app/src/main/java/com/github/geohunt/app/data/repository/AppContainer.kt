package com.github.geohunt.app.data.repository

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
class AppContainer {
    val database = Firebase.database
    val storage = Firebase.storage

    val image = ImageRepository()
    val auth = AuthRepository()
    val user = UserRepository(image, auth)
}