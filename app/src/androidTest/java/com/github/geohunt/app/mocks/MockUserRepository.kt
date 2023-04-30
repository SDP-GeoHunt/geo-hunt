package com.github.geohunt.app.mocks

import com.firebase.ui.auth.IdpResponse
import com.github.geohunt.app.data.exceptions.UserNotFoundException
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.EditedUser
import com.github.geohunt.app.model.User

open class MockUserRepository(private val userRepository: UserRepository? = null): UserRepositoryInterface {
    override suspend fun createUserIfNew(identity: IdpResponse) {
        userRepository?.createUserIfNew(identity)
    }

    override suspend fun getCurrentUser(): User {
        return MockAuthRepository.defaultLoggedUser
    }

    override suspend fun getUser(id: String): User {
        if (id == "1") return User("1", "dn", null)
        if (id == "2") return User("2", "dn2", null)
        return userRepository?.getUser(id) ?: throw UserNotFoundException(id)
    }

    override suspend fun updateUser(editedUser: EditedUser) {
        return userRepository?.updateUser(editedUser) ?: Unit
    }

    override fun getProfilePictureUrl(user: User): String? {
        return userRepository?.getProfilePictureUrl(user) ?: "https://picsum.photos/200"
    }
}