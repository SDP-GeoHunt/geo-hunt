package com.github.geohunt.app.model.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

object FirebaseEmulator {
    fun getEmulatedFirebase(): FirebaseDatabase {
        val instance = Firebase.database
        instance.useEmulator("10.0.2.2", 9000)
        return instance
    }

    fun getEmulatedStorage() : FirebaseStorage {
        val storageInstance = FirebaseStorage.getInstance("gs://geohunt-1.appspot.com")
        storageInstance.useEmulator("10.0.2.2", 9199)
        return storageInstance
    }
}