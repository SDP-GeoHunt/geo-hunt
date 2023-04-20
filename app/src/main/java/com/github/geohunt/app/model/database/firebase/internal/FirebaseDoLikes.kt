package com.github.geohunt.app.model.database.firebase.internal

import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.model.database.firebase.getChallengeRefFromId
import com.github.geohunt.app.utility.convertTo
import com.github.geohunt.app.utility.thenDo
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot

/**
 * Updates the like for a given user (characterized by @p uid) on a given challenge (characterized by @p cid).
 *
 * @warning This method is subject to change and should not be used, only use [FirebaseLoggedUserContext]
 */
internal fun doLike(
    database : FirebaseDatabase,
    uid: String,
    cid: String,
    like: Boolean = true
) : Task<Nothing?> {
    // TODO Writes are not made atomically and should be batched instead
    //      See https://github.com/SDP-GeoHunt/geo-hunt/issues/88#issue-1647852411

    val userLikesRef = database.dbUserRef.child(uid).child("likes")
    val challengeLikesRef = database.dbChallengeRef.getChallengeRefFromId(cid).child("likedBy")

    val defaultMap = emptyMap<String, Boolean>().withDefault { false }
    val userLikesTask = userLikesRef.get()
    val challengeLikesTask = challengeLikesRef.get()

    return Tasks.whenAllSuccess<DataSnapshot>(
        userLikesTask,
        challengeLikesTask
    )
        .thenDo {
            val userLikes = it[0].convertTo<Map<String, Boolean>>() ?: defaultMap
            val challengeLikes = it[1].convertTo<Map<String, Boolean>>() ?: defaultMap

            // Abort if the user already likes the challenge
            // or if the user tries to unlike a challenge not liked
            if (!(like xor (userLikes[cid] == true))) {
                return@thenDo Tasks.forResult(null)
            }

            Tasks.whenAll(
                //  Update the user's likes list
                userLikesRef.setValue(if (like) (userLikes + (cid to true)) else userLikes - cid),

                // Update the challenge's likes list
                challengeLikesRef.setValue(if (like) (challengeLikes + (uid to true)) else challengeLikes - uid)
            ).thenMap { null }
        }
}