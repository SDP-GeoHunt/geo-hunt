package com.github.geohunt.app.model.database.api

import android.graphics.Bitmap

/**
 * Represents a set of modifications on an existing user.
 */
data class EditedUser(
    val user: User,
    var displayName: String,
    var profilePicture: Bitmap? = null
) {
    /**
     * Sets the profile picture
     */
    fun setProfilePicture(profilePicture: Bitmap): EditedUser {
        return copy(profilePicture = profilePicture)
    }

    companion object {
        fun fromUser(user: User): EditedUser {
            return EditedUser(user, user.name)
        }
    }
}