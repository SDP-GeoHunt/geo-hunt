package com.github.geohunt.app.model.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Singleton

object FirebaseEmulator {
    @Singleton
    fun getEmulatedFirebase(): FirebaseDatabase {
        val database = Firebase.database
        database.useEmulator("10.0.2.2", 9000)
        return database
    }

    fun getEmulatedStorage() : FirebaseStorage {
        val storageInstance = FirebaseStorage.getInstance("gs://geohunt-1.appspot.com")
        storageInstance.useEmulator("10.0.2.2", 9199)
        return storageInstance
    }
}