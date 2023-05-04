package com.github.geohunt.app.data.repository

import com.firebase.ui.auth.IdpResponse
import com.github.geohunt.app.data.exceptions.UserNotFoundException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.model.EditedUser
import com.github.geohunt.app.model.User

interface UserRepositoryInterface {
    /**
     * Creates a new user in Firebase's Realtime Database from the given Identity Provider (IDP)
     * response.
     *
     * The user must be logged in, otherwise a [UserNotLoggedInException] is thrown.
     *
     * If the user already exists, this method is a nop.
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun createUserIfNew(identity: IdpResponse)

    /**
     * Returns the current user as described in the Firebase RTDB
     */
    suspend fun getCurrentUser(): User

    /**
     * Returns the user with the given ID.
     *
     * If there are none, throws a [UserNotFoundException].
     *
     * @param id The user unique ID.
     * @throws UserNotFoundException if there are no users with the given id.
     */
    @Throws(UserNotFoundException::class)
    suspend fun getUser(id: String): User

    /**
     * Updates the user in Firebase's RTDB with the given Edited User.
     */
    suspend fun updateUser(editedUser: EditedUser)

    fun getProfilePictureUrl(user: User): String?
}