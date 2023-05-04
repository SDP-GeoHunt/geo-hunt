package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.ProfileVisibilityRepositoryInterface
import com.github.geohunt.app.model.database.api.ProfileVisibility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class MockProfileVisibilityRepository: ProfileVisibilityRepositoryInterface {
    override suspend fun getProfileVisibility(uid: String): Flow<ProfileVisibility> {
        return flowOf(ProfileVisibility.PUBLIC)
    }

    override suspend fun setProfileVisibility(uid: String, visibility: ProfileVisibility) {

    }
}