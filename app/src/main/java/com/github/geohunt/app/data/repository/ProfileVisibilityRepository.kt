package com.github.geohunt.app.data.repository

import com.github.geohunt.app.model.database.api.ProfileVisibility
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProfileVisibilityRepository(
    database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ProfileVisibilityRepositoryInterface {
    private val visibilities = database.getReference("profileVisibilities")

    override suspend fun getProfileVisibility(uid: String): Flow<ProfileVisibility> {
        return visibilities.child(uid)
            .snapshots
            .map { it.getValue(Int::class.java) ?: 0 }
            .map { it.coerceIn(0, ProfileVisibility.values().size - 1) }
            .map { ProfileVisibility.values()[it] }
            .flowOn(ioDispatcher)
    }

    override suspend fun setProfileVisibility(uid: String, visibility: ProfileVisibility) {
        withContext(ioDispatcher) {
            visibilities.child(uid).setValue(visibility.ordinal)
        }
    }
}