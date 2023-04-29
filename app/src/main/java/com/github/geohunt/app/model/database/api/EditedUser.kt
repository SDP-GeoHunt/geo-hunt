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
    companion object {
        fun fromUser(user: User): EditedUser {
            return EditedUser(user, user.name)
        }
    }
}