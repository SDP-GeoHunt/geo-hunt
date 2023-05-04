package com.github.geohunt.app.data.repository

import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.database.api.ProfileVisibility
import kotlinx.coroutines.flow.Flow

interface ProfileVisibilityRepositoryInterface {
    /**
     * Returns the visibility of the profile of the given user
     *
     * @param user The user
     */
    suspend fun getProfileVisibility(user: User): Flow<ProfileVisibility> = getProfileVisibility(user)

    /**
     * Returns the visibility of the profile of the given user
     *
     * @param uid The user's uid
     */
    suspend fun getProfileVisibility(uid: String): Flow<ProfileVisibility>

    /**
     * Sets the visibility of the profile of the given user
     *
     * @param user The user
     * @param visibility The new profile's visibility
     */
    suspend fun setProfileVisibility(user: User, visibility: ProfileVisibility): Unit = setProfileVisibility(user, visibility)

    /**
     * Sets the visibility of the profile of the given user
     *
     * @param uid The user's uid
     * @param visibility The new profile's visibility
     */
    suspend fun setProfileVisibility(uid: String, visibility: ProfileVisibility)
}
