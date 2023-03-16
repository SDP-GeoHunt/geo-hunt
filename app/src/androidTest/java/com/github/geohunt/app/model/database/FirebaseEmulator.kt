package com.github.geohunt.app.model.database

import com.github.geohunt.app.model.database.firebase.FirebaseSingletons
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

object FirebaseEmulator {
    fun init() {
        val databaseInstance = FirebaseDatabase.getInstance("http://10.0.2.2:9000/?ns=geohunt-1-default-rtdb").reference
        FirebaseSingletons.database.set(databaseInstance)

        val storageInstance = FirebaseStorage.getInstance("gs://geohunt-1.appspot.com")
        storageInstance.useEmulator("10.0.2.2", 9199)
        FirebaseSingletons.storage.set(storageInstance.reference)
    }
}