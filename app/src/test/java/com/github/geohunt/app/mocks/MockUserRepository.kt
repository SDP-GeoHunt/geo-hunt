package com.github.geohunt.app.mocks

import com.firebase.ui.auth.IdpResponse
import com.github.geohunt.app.data.exceptions.UserNotFoundException
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.EditedUser
import com.github.geohunt.app.model.User

open class MockUserRepository(
    private val userRepository: UserRepository? = null,
    private val mockAuth: MockAuthRepository? = null,
): UserRepositoryInterface {
    override suspend fun createUserIfNew(identity: IdpResponse) {
        userRepository?.createUserIfNew(identity)
    }

    override suspend fun getCurrentUser(): User {
        return mockAuth?.loggedUser ?: MockAuthRepository.defaultLoggedUser
    }

    override suspend fun getUser(id: String): User {
        if (id == "1") return user1
        if (id == "2") return user2
        if (id == "3") return user3
        return userRepository?.getUser(id) ?: throw UserNotFoundException(id)
    }

    override suspend fun updateUser(editedUser: EditedUser) {
        return userRepository?.updateUser(editedUser) ?: Unit
    }

    override fun getProfilePictureUrl(user: User): String? {
        return userRepository?.getProfilePictureUrl(user) ?: ""
    }

    companion object {
        val user1 = User("1", "dn", null)
        val user2 = User("2", "dn2", null)
        val user3 = User("3", "dn3", null)
    }
}