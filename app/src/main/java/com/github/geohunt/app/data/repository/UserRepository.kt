package com.github.geohunt.app.data.repository

import com.firebase.ui.auth.IdpResponse
import com.github.geohunt.app.data.exceptions.UserNotFoundException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.network.firebase.models.FirebaseUser
import com.github.geohunt.app.model.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Contains methods related to the retrieval of users.
 *
 * As Firebase Auth does not provide any method to get the information of other users, data is
 * stored in Firebase's Realtime Database instead.
 *
 * @see [User]
 * @see [FirebaseUser]
 */
class UserRepository(
    private val imageRepository: ImageRepository,
    private val authRepository: AuthRepository,
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val users = database.getReference("users")

    /**
     * Creates a new user in Firebase's Realtime Database from the given Identity Provider (IDP)
     * response.
     *
     * The user must be logged in, otherwise a [UserNotLoggedInException] is thrown.
     *
     * If the user already exists, this method is a nop.
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun createUserIfNew(identity: IdpResponse) {
        authRepository.requireLoggedIn()

        if (identity.isNewUser) {
            val newUser = authRepository.getCurrentUser()

            val newEntry = FirebaseUser(
                id = newUser.id,
                displayName = newUser.displayName,
                profilePictureUrl = newUser.profilePictureUrl
            )

            withContext(ioDispatcher) {
                users.child(newEntry.id)
                    .setValue(newEntry)
                    .await()
            }
        }
    }

    private fun FirebaseUser.asExternalModel(): User = User(
        id = id,
        displayName = displayName,
        profilePictureUrl = profilePictureUrl
    )

    /**
     * Returns the user with the given ID.
     *
     * If there are none, throws a [UserNotFoundException].
     *
     * @param id The user unique ID.
     * @throws UserNotFoundException if there are no users with the given id.
     */
    @Throws(UserNotFoundException::class)
    suspend fun getUser(id: String): User = withContext(ioDispatcher) {
        users.child(id)
            .get()
            .await()
            .getValue(FirebaseUser::class.java)
            ?.asExternalModel() ?: throw UserNotFoundException(id)
    }

    fun getProfilePictureUrl(user: User): String = imageRepository.getProfilePictureUrl(user)
}