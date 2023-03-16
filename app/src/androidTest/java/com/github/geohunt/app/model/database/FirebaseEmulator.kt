package com.github.geohunt.app.model.database

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseEmulator {
    fun init() {
        try {
            val firebaseDatabase = Firebase.database
            firebaseDatabase.useEmulator("10.0.2.2", 9000)

            val firebaseStorage = Firebase.storage
            firebaseStorage.useEmulator("10.0.2.2", 9199)
        }
        catch(ignored: IllegalStateException) {}
    }
}