package com.github.geohunt.app.model.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object FirebaseEmulator {
    fun init() {
        FirebaseDatabase.getInstance("http://10.0.2.2:9000/?ns=geohunt-1-default-rtdb").reference
        val storageInstance = FirebaseStorage.getInstance("gs://geohunt-1.appspot.com")
        storageInstance.useEmulator("10.0.2.2", 9199)
    }

    fun getEmulatedFirebase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance("http://10.0.2.2:9000/?ns=geohunt-1-default-rtdb")
    }

    fun getEmulatedStorage() : FirebaseStorage {
        val storageInstance = FirebaseStorage.getInstance("gs://geohunt-1.appspot.com")
        storageInstance.useEmulator("10.0.2.2", 9199)
        return storageInstance
    }
}