package com.github.geohunt.app.model

import com.github.geohunt.app.data.local.LocalPicture

data class User(
    val id: String,
    val displayName: String?,
    val profilePictureUrl: String?
)

/**
 * Represents a sets of changes on the user.
 */
data class EditedUser(
    val user: User,
    var newDisplayName: String? = null,
    var newProfilePicture: LocalPicture? = null
) {
    companion object {
        fun fromUser(user: User): EditedUser {
            return EditedUser(user, user.displayName, null)
        }
    }
}