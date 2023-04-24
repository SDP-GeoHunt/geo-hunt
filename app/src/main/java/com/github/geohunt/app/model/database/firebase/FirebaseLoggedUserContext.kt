package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.R
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.DataPool
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.api.*
import com.github.geohunt.app.model.database.firebase.internal.doFollow
import com.github.geohunt.app.model.database.firebase.internal.doJoinHunt
import com.github.geohunt.app.model.database.firebase.internal.doLike
import com.github.geohunt.app.model.database.firebase.internal.doUpdateProfileVisibility
import com.github.geohunt.app.model.database.firebase.internal.doUpdateUser
import com.github.geohunt.app.utility.DateUtils
import com.github.geohunt.app.utility.convertTo
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.time.LocalDateTime

/**
 * This instance should only be instanced once per user
 */
internal class FirebaseLoggedUserContext(
    val database: FirebaseDatabase,
    override val loggedUserRef: LiveLazyRef<User>
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

    private val doesLikeMapByCID = DataPool<String, LazyRef<Boolean>> { cid ->
        object : BaseLazyRef<Boolean>() {
            override val id: String
                get() = "$cid!like!${loggedUserRef.id}"

            override fun fetchValue(): Task<Boolean> {
                val ref = database.dbUserRef.child(loggedUserRef.id).child("likes").child(cid)

                //TODO: Currently update are not supported as this value is actually cached
                // as such, in the future we should register an "on value change" but only
                // when the user is displayed
                return ref.get().thenMap { dataSnapshot ->
                    dataSnapshot.convertTo<Boolean>() ?: false
                }
            }
        }
    }

    override val User.doesFollow: LazyRef<Boolean>
        get() = doesFollowMapByUID.get(this.uid)

    override val Challenge.doesLoggedUserLikes: LazyRef<Boolean>
        get() = doesLikeMapByCID.get(this.cid)

    override fun User.follow(): Task<Nothing?> {
        return doFollow(database, loggedUserRef.id, this.uid, true)
    }

    override fun LazyRef<User>.follow(): Task<Nothing?> {
        return doFollow(database, loggedUserRef.id, this.id, true)
    }

    override fun User.unfollow(): Task<Nothing?> {
        return doFollow(database, loggedUserRef.id, this.uid, false)
    }

    override fun LazyRef<User>.unfollow(): Task<Nothing?> {
        return doFollow(database, loggedUserRef.id, this.id, false)
    }

    override fun Challenge.like(): Task<Nothing?> {
        return doLike(database, loggedUserRef.id, this.cid, true)
    }

    override fun Challenge.unlike(): Task<Nothing?> {
        return doLike(database, loggedUserRef.id, this.cid, false)
    }

    override fun Challenge.joinHunt(): Task<Nothing?> {
        return doJoinHunt(database, loggedUserRef.id, this.cid, true)
    }

    override fun Challenge.leaveHunt(): Task<Nothing?> {
        return doJoinHunt(database, loggedUserRef.id, this.cid, false)
    }

    override fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        difficulty: Challenge.Difficulty,
        expirationDate: LocalDateTime?,
        description: String?
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
            claims = mapOf(),
            location = location
        )

        // Get the reference to the thumbnail Bitmap and set the value
        val thumbnailBitmap = database.getChallengeThumbnailById(challengeId)
        thumbnailBitmap.value = thumbnail

        // Create both jobs (update database, update storage)
        val submitToDatabaseTask = dbChallengeRef.setValue(challengeEntry)
        val submitToStorageTask = thumbnailBitmap.saveToLocalStorageThenSubmit()
        val pushChallengeToUserTask = database.dbUserRef.child(loggedUserRef.id)
            .child("challenges")
            .child(challengeId).setValue(true)

        // Finally make the completable task that succeed if both task succeeded
        return Tasks.whenAll(submitToDatabaseTask, submitToStorageTask, pushChallengeToUserTask)
            .thenMap {
                FirebaseChallenge(
                    cid = challengeId,
                    author = loggedUserRef,
                    thumbnail = thumbnailBitmap,
                    publishedDate = DateUtils.localFromUtcIso8601(challengeEntry.publishedDate!!),
                    expirationDate = expirationDate,
                    correctLocation = location,
                    difficulty = difficulty,
                    description = description
                )
            }
    }

    override fun updateLoggedUser(editedUser: EditedUser): Task<Nothing?> {
        return doUpdateUser(database, loggedUserRef.id, editedUser)
    }

    override fun setProfileVisibility(profileVisibility: ProfileVisibility): Task<Void> {
        return doUpdateProfileVisibility(database, loggedUserRef.id, profileVisibility)
    }

    override fun Challenge.submitClaim(thumbnail: Bitmap, location: Location): Task<Claim> {
        require(thumbnail.width * thumbnail.height < R.integer.maximum_number_of_pixel_per_photo)

        // State variable
        val dbChallengeRef = database.dbChallengeRef.getChallengeRefFromId(cid)
        val dbClaimRef = database.dbClaimRef.child(cid).push()
        val claimId = cid + "!!" + dbClaimRef.key!!

        val claimEntry = ClaimEntry(
            user = loggedUserRef.id,
            cid = cid,
            time = DateUtils.utcIso8601Now(),
            location = location
        )

        // Get the reference to the thumbnail Bitmap and set the value
        val thumbnailBitmap = database.getClaimThumbnailById(claimId)
        thumbnailBitmap.value = thumbnail

        // Create both jobs (update database, update storage, update claim list)
        val submitToDatabaseTask = dbClaimRef.setValue(claimEntry)
        val submitToStorageTask = thumbnailBitmap.saveToLocalStorageThenSubmit()
        val pushChallengeListTask = dbChallengeRef.child("claims").child(claimId).setValue(true)

        // Finally make the completable task that succeed if both task succeeded
        return Tasks.whenAll(submitToDatabaseTask, submitToStorageTask, pushChallengeListTask)
            .thenMap {
                FirebaseClaim(
                    id = claimId,
                    user = loggedUserRef,
                    time = DateUtils.localFromUtcIso8601(claimEntry.time),
                    challenge = database.getChallengeById(this.cid),
                    location = location,
                    image = thumbnailBitmap
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