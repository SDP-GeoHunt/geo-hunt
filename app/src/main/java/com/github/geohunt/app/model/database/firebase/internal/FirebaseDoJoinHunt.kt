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
 * Updates the database to join the hunt for a specific challenge and a specific user
 *
 * @note To ensure easier data querying, the data must be synchronized at 3 places in the database :
 *       - The number of followers in the challenge (followers = activeHunters)
 *       - The list of active hunt for the current user
 *
 * @warning This method is subject to change and should not be used, only use [FirebaseLoggedUserContext]
 */
internal fun doJoinHunt(database: FirebaseDatabase, uid: String, cid: String, isJoin: Boolean): Task<Nothing?> {
    // TODO Writes are not made atomically and should be batched instead
    //      See https://github.com/SDP-GeoHunt/geo-hunt/issues/88#issue-1647852411
    val activeHuntsListRef = database.dbUserRef.child(uid).child("activeHunts")
    val followersNumberRef =
        database.dbChallengeRef.getChallengeRefFromId(cid).child("numberOfActiveHunters")

    val activeHuntsListTask = activeHuntsListRef.get()
    val followersNumberTask = followersNumberRef.get()

    return Tasks.whenAllSuccess<DataSnapshot>(activeHuntsListTask, followersNumberTask).thenDo {
        val activeHuntsList = it[0].convertTo<Map<String, Boolean>>()
            ?: mapOf<String, Boolean>().withDefault { false }
        val followersNumber = it[1].convertTo<Int>() ?: 0

        if (!(isJoin xor (activeHuntsList[cid] == true))) {
            return@thenDo Tasks.forResult(null) // Already the case
        }

        Tasks.whenAll(
            // Increment the number of active hunts within the challenge
            followersNumberRef.setValue(followersNumber + if (isJoin) 1 else -1),

            // Add the challenge to the list of followed challenge
            activeHuntsListRef.setValue(
                if (isJoin) activeHuntsList + (cid to true)
                else activeHuntsList - cid
            )
        ).thenMap { null }
    }
}