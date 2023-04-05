package com.github.geohunt.app.ui.components.profile.edit

import android.graphics.Bitmap
import com.github.geohunt.app.model.database.api.User

/**
 * Represents a set of modifications on an existing user.
 */
data class EditedUser(
    var displayName: String,
    var profilePicture: Bitmap? = null,
    var isProfilePictureNew: Boolean = false
) {
    /**
     * Apply all updates except profile picture
     */
    fun applyUpdates(user: User): User {
        user.displayName = this.displayName
        return user
    }

    /**
     * Sets the profile picture
     */
    fun setProfilePicture(profilePicture: Bitmap): EditedUser {
        this.profilePicture = profilePicture
        this.isProfilePictureNew = true
        return this
    }

    companion object {
        fun fromUser(user: User): EditedUser {
            return EditedUser(user.name)
        }
    }
}