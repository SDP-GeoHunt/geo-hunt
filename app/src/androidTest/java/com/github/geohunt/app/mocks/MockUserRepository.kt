package com.github.geohunt.app.mocks

import com.firebase.ui.auth.IdpResponse
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.EditedUser
import com.github.geohunt.app.model.User

open class MockUserRepository(private val userRepository: UserRepository): UserRepositoryInterface {
    override suspend fun createUserIfNew(identity: IdpResponse) {
        return userRepository.createUserIfNew(identity)
    }

    override suspend fun getCurrentUser(): User {
        return userRepository.getCurrentUser()
    }

    override suspend fun getUser(id: String): User {
        if (id == "1") return User("1", "dn", null)
        return userRepository.getUser(id)
    }

    override suspend fun updateUser(editedUser: EditedUser) {
        return userRepository.updateUser(editedUser)
    }

    override fun getProfilePictureUrl(user: User): String? {
        return userRepository.getProfilePictureUrl(user)
    }
}