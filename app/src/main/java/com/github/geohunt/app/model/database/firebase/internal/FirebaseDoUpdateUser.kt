package com.github.geohunt.app.model.database.firebase.internal

import com.github.geohunt.app.model.database.api.EditedUser
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

/**
 * Update the metadata for the currently logged user
 *
 * @warning this function should not be directly used. Please prefer the use of [LoggedUserContext]
 */
internal fun doUpdateUser(
    database: FirebaseDatabase,
    uid: String,
    editedUser: EditedUser
): Task<Nothing?> {
    val userRef = database.dbUserRef.child(uid)
    val updateNameFieldTask =
        userRef.child("displayName").setValue(editedUser.displayName)

    if (editedUser.profilePicture != null) {
        val hash = BitmapUtils.hash(editedUser.profilePicture!!)
        val ppRef = database.getProfilePicture(uid, hash)
        ppRef.value = editedUser.profilePicture

        return Tasks.whenAllSuccess<Any>(
            updateNameFieldTask,

            // Save the profile picture to the storage
            ppRef.saveToLocalStorageThenSubmit(),

            // Update the hash field of the user
            userRef.child("profilePictureHash").setValue(hash)
        ).thenMap { null }
    } else {
        return updateNameFieldTask.thenMap { null }
    }
}
