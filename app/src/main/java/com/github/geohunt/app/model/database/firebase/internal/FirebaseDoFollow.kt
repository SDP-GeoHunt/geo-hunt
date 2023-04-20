package com.github.geohunt.app.model.database.firebase.internal

import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.convertTo
import com.github.geohunt.app.utility.thenDo
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot

/**
 * Updates the database to (un)follow the given users.
 *
 * @note To ensure easier data querying, the data must be synchronized at 3 places in the database :
 *       - In the follower's follow list, the followee should be added/removed,
 *       - The followee's counter should be incremented/decremented,
 *       - The follower should be added/removed to the followee's follower list.
 *
 * @warning This method is subject to change and should not be used, only use [FirebaseLoggedUserContext]
 */
internal fun doFollow(
    database : FirebaseDatabase,
    follower: String,
    followee: String,
    follow: Boolean = true
) : Task<Nothing?> {
    // TODO Writes are not made atomically and should be batched instead
    //      See https://github.com/SDP-GeoHunt/geo-hunt/issues/88#issue-1647852411

    val followerListRef = database.dbUserRef.child(follower).child("followList")
    val counterRef = database.dbUserRef.child(followee).child("numberOfFollowers")
    val follows = database.dbFollowersRef.child(followee)

    val defaultMap = emptyMap<String, Boolean>().withDefault { false }
    val followerListTask = followerListRef.get()
    val followedCounterTask = counterRef.get()
    val followsPairsTask = follows.get()

    return Tasks.whenAllSuccess<DataSnapshot>(
        followerListTask,
        followedCounterTask,
        followsPairsTask
    )
        .thenDo {
            val followerList = it[0].convertTo<Map<String, Boolean>>() ?: defaultMap
            val followedCounter = it[1].convertTo<Int>() ?: 0
            val followsPairs = it[2].convertTo<Map<String, Boolean>>() ?: defaultMap

            // Abort if the user already follows the followee
            // or if the user tries to unfollow someone not followed
            if (!(follow xor (followerList[followee] == true))) {
                return@thenDo Tasks.forResult(null)
            }

            Tasks.whenAll(
                // Update the follower's follow list
                followerListRef.setValue(if (follow) (followerList + (followee to true)) else followerList - followee),

                // Update the follow counter of the followee
                counterRef.setValue(
                    if (follow) followedCounter + 1 else Integer.max(
                        followedCounter - 1,
                        0
                    )
                ),

                // Update the follows relationship
                follows.setValue(if (follow) followsPairs + (follower to true) else followsPairs - follower)
            ).thenMap { null }
        }
}
