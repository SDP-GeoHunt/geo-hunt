package com.github.geohunt.app.model.database.firebase

import com.github.geohunt.app.utility.Singleton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseSingletons {
    val storage = Singleton(Firebase.storage("gs://geohunt-1.appspot.com").reference)
    val database = Singleton(Firebase.database.reference)
}
