package com.github.geohunt.app.data.repository.bounties

import com.github.geohunt.app.data.network.firebase.toList
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ActiveBountiesRepository(
        database: FirebaseDatabase,
        private val authRepository: AuthRepositoryInterface,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ActiveBountiesRepositoryInterface {
    private val bountiesOfRef = database.getReference("bounties/ofUser")

    override suspend fun joinBounty(bid: String) {
        updateBountyState(bid,true)
    }

    override suspend fun leaveBounty(bid: String) {
        updateBountyState(bid,false)
    }

    private suspend fun updateBountyState(bid: String, newState: Boolean) = withContext(ioDispatcher) {
        authRepository.requireLoggedIn()
        @Suppress("DEPRECATION") val uid = authRepository.getCurrentUser().id

        val newVal = if (newState) true else null
        bountiesOfRef
                .child(uid)
                .child(bid)
                .setValue(newVal)
                .await()
    }

    override fun getBounties(): Flow<List<String>> {
        authRepository.requireLoggedIn()
        @Suppress("DEPRECATION") val uid = authRepository.getCurrentUser().id

        return bountiesOfRef
                .child(uid)
                .snapshots
                .map {
                    it.toList()
                }
                .flowOn(ioDispatcher)
    }
}