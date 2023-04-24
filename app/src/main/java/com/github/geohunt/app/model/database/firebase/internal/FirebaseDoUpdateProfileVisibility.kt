package com.github.geohunt.app.model.database.firebase.internal

import com.github.geohunt.app.model.database.api.ProfileVisibility
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.google.android.gms.tasks.Task

/**
 * Updates profile visibility of an user.
 *
 * @param database The firebase database
 * @param uid User's uid
 * @param visibility The new visibility for the user
 */
internal fun doUpdateProfileVisibility(
    database: FirebaseDatabase,
    uid: String,
    visibility: ProfileVisibility
): Task<Void> {
    return database.dbUserRef.child(uid).child("profileVisibility").setValue(visibility.ordinal)
}