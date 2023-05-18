package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.ProfileVisibilityRepositoryInterface
import com.github.geohunt.app.model.database.api.ProfileVisibility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

open class MockProfileVisibilityRepository: ProfileVisibilityRepositoryInterface {
    private val map: MutableMap<String, MutableStateFlow<ProfileVisibility>> = HashMap()

    override suspend fun getProfileVisibility(uid: String): Flow<ProfileVisibility> {
        if (this.map[uid] == null) {
            this.map[uid] = MutableStateFlow(ProfileVisibility.PUBLIC)
        }
        return this.map[uid]!!.asStateFlow().map { it }
    }

    override suspend fun setProfileVisibility(uid: String, visibility: ProfileVisibility) {
        if (this.map[uid] == null) {
            this.map[uid] = MutableStateFlow(ProfileVisibility.PUBLIC)
        }
        this.map[uid]!!.value = visibility
    }
}