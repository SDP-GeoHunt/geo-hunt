package com.github.geohunt.app.model.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * This class provides utility methods for detecting connection state to Firebase's database,
 * which can be listened to detect disconnection/reconnection events.
 */
class NetworkMonitor(database: FirebaseDatabase) {
    val isConnected: Flow<Boolean> =
        // This is a special reference generated by Firebase itself
        // See https://firebase.google.com/docs/database/android/offline-capabilities#section-connection-state
        database.getReference(".info/connected")
               .snapshots
               .map { it.getValue(Boolean::class.java) ?: false }
               .flowOn(Dispatchers.IO)
}
