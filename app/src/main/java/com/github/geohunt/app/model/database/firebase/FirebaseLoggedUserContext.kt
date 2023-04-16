package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.R
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.DataPool
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.*
import com.github.geohunt.app.utility.DateUtils
import com.github.geohunt.app.utility.convertTo
import com.github.geohunt.app.utility.thenDo
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import java.time.LocalDateTime

/**
 * This instance should only be instanced once per user
 */
internal class FirebaseLoggedUserContext(
    val database: FirebaseDatabase,
    override val loggedUserRef: LazyRef<User>
) : LoggedUserContext {

    private val doesFollowMapByUID = DataPool<String, LazyRef<Boolean>> { uid ->
        object : BaseLazyRef<Boolean>() {
            override val id: String
                get() = "$uid!follow!${loggedUserRef.id}"

            override fun fetchValue(): Task<Boolean> {
                val ref = database.dbUserRef.child(loggedUserRef.id).child("followList").child(uid)

                //TODO: Currently update are not supported as this value is actually cached
                // as such, in the future we should register an "on value change" but only
                // when the user is displayed
                return ref.get().thenMap { dataSnapshot ->
                    dataSnapshot.convertTo<Boolean>() ?: false
                }
            }
        }
    }

    /**
     * Updates the database to (un)follow the given users.
     *
     * @note To ensure easier data querying, the data must be synchronized at 3 places in the database :
     *       - In the follower's follow list, the followee should be added/removed,
     *       - The followee's counter should be incremented/decremented,
     *       - The follower should be added/removed to the followee's follower list.
     */
    private fun doFollow(
        follower: String,
        followee: String,
        follow: Boolean = true
    ): Task<Nothing?> {
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

    override val User.doesFollow: LazyRef<Boolean>
        get() = doesFollowMapByUID.get(this.uid)

    override fun User.follow(): Task<Nothing?> {
        return doFollow(loggedUserRef.id, this.uid, true)
    }

    override fun LazyRef<User>.follow(): Task<Nothing?> {
        return doFollow(loggedUserRef.id, this.id, true)
    }

    override fun User.unfollow(): Task<Nothing?> {
        return doFollow(loggedUserRef.id, this.uid, false)
    }

    override fun LazyRef<User>.unfollow(): Task<Nothing?> {
        return doFollow(loggedUserRef.id, this.id, false)
    }

    private fun doJoinHunt(uid: String, cid: String, isJoin: Boolean): Task<Nothing?> {
        // TODO Writes are not made atomically and should be batched instead
        //      See https://github.com/SDP-GeoHunt/geo-hunt/issues/88#issue-1647852411
        val activeHuntsListRef = database.dbUserRef.child(uid).child("activeHunts")
        val followersNumberRef =
            database.dbChallengeRef.getChallengeFromId(cid).child("numberOfActiveHunters")

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

    override fun Challenge.joinHunt(): Task<Nothing?> {
        return doJoinHunt(loggedUserRef.id, this.cid, true)
    }

    override fun Challenge.leaveHunt(): Task<Nothing?> {
        return doJoinHunt(loggedUserRef.id, this.cid, false)
    }

    override fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        expirationDate: LocalDateTime?
    ): Task<Challenge> {
        // Requirements
        require(thumbnail.width * thumbnail.height < R.integer.maximum_number_of_pixel_per_photo)

        // State variable
        val coarseHash = location.getCoarseHash()
        val dbChallengeRef = database.dbChallengeRef.child(coarseHash).push()
        val challengeId = coarseHash + dbChallengeRef.key!!

        val challengeEntry = ChallengeEntry(
            authorId = loggedUserRef.id,
            publishedDate = DateUtils.utcIso8601Now(),
            expirationDate = DateUtils.utcIso8601FromLocalNullable(expirationDate),
            claims = listOf(),
            location = location
        )

        // Get the reference to the thumbnail Bitmap and set the value
        val thumbnailBitmap = database.getThumbnailRefById(challengeId)
        thumbnailBitmap.value = thumbnail

        // Create both jobs (update database, update storage)
        val submitToDatabaseTask = dbChallengeRef.setValue(challengeEntry)
        val submitToStorageTask = thumbnailBitmap.saveToLocalStorageThenSubmit()

        // Finally make the completable task that succeed if both task succeeded
        return Tasks.whenAll(submitToDatabaseTask, submitToStorageTask).thenMap {
            FirebaseChallenge(
                cid = challengeId,
                author = loggedUserRef,
                thumbnail = thumbnailBitmap,
                publishedDate = DateUtils.localFromUtcIso8601(challengeEntry.publishedDate!!),
                expirationDate = expirationDate,
                correctLocation = location,
                claims = listOf()
            )
        }
    }

    override fun Challenge.submitClaim(thumbnail: Bitmap, location: Location): Task<Claim> {
        require(thumbnail.width * thumbnail.height < R.integer.maximum_number_of_pixel_per_photo)

        // State variable
        val coarseHash = location.getCoarseHash()
        val dbClaimRef = database.dbClaimRef.child(coarseHash).push()
        val claimId = coarseHash + dbClaimRef.key!!

        val claimEntry = ClaimEntry(
            user = loggedUserRef.id,
            challenge = this,
            time = DateUtils.utcIso8601Now(),
            location = location
        )

        // Get the reference to the thumbnail Bitmap and set the value
        val thumbnailBitmap = database.getThumbnailRefById(claimId)
        thumbnailBitmap.value = thumbnail

        // Create both jobs (update database, update storage)
        val submitToDatabaseTask = dbClaimRef.setValue(claimEntry)
        val submitToStorageTask = thumbnailBitmap.saveToLocalStorageThenSubmit()

        // Finally make the completable task that succeed if both task succeeded
        return Tasks.whenAll(submitToDatabaseTask, submitToStorageTask).thenMap {
            FirebaseClaim(
                id = claimId,
                user = loggedUserRef,
                time = DateUtils.localFromUtcIso8601(claimEntry.time!!),
                challenge = database.getChallengeById(this.cid),
                location = location
            )
        }
    }

    override fun getFollowedUsers(): Task<List<LazyRef<User>>> {
        return loggedUserRef.fetch().thenMap {
            it.followList
        }
    }

    override fun getFollowers(): Task<List<LazyRef<User>>> {
        return database.getFollowersOf(loggedUserRef.id)
    }
}