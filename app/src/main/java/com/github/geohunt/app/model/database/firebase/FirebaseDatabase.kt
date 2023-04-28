package com.github.geohunt.app.model.database.firebase

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.R
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.DataPool
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.*
import com.github.geohunt.app.utility.*
import com.github.geohunt.app.utility.DateUtils.localFromUtcIso8601
import com.github.geohunt.app.utility.DateUtils.utcIso8601FromLocalNullable
import com.github.geohunt.app.utility.DateUtils.utcIso8601Now
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.tasks.await
import okhttp3.internal.toImmutableList
import java.io.File
import java.lang.Integer.max
import java.time.LocalDateTime

class FirebaseDatabase(activity: Activity) : Database {
    private val database = FirebaseSingletons.database.get()
    private val storage = FirebaseSingletons.storage.get()
    private val currentUser : String
        get() = Authenticator.authInstance.get().user!!.uid

    // Database references
    internal val dbUserRef = database.child("users")
    internal val dbChallengeRef = database.child("challenges")
    internal val dbFollowersRef = database.child("followers")
    internal val dbClaimRef = database.child("claims")

    // Storage references
    internal val storageImagesRef = storage.child("images")

    // Local Folders
    internal val localImageFolder : File = activity.getExternalFilesDir("images")!!

    // Create the object pool in order to save some memory
    private val userRefById = DataPool<String, FirebaseUserRef> {
        FirebaseUserRef(it, this)
    }

    private val challengeRefById = DataPool<String, FirebaseChallengeRef> {
        FirebaseChallengeRef(it, this)
    }

    private val imageRefById = DataPool<String, FirebaseBitmapRef> {
        FirebaseBitmapRef(it, this)
    }

    private val claimRefById = DataPool<String, FirebaseClaimRef> {
        FirebaseClaimRef(it, this)
    }

    init {
        if (!localImageFolder.exists()) {
            localImageFolder.mkdirs()
        }
    }

    /**
     * Creates a new Challenge and stores it to the Firebase Database and Storage.
     * Notice that the given bitmap should not have more pixel than
     * [R.integer.maximum_number_of_pixel_per_photo] otherwise this function will throw
     * an [IllegalArgumentException]
     *
     * @param thumbnail the thumbnail of the challenge
     * @param location the location of the challenge
     * @param expirationDate the expiration date of the challenge, can be null
     * @return a Task that will complete with the created Challenge
     */
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
        val dbChallengeRef = dbChallengeRef.child(coarseHash).push()
        val challengeId = coarseHash + dbChallengeRef.key!!

        val challengeEntry = ChallengeEntry(
            authorId = currentUser,
            publishedDate = utcIso8601Now(),
            expirationDate = utcIso8601FromLocalNullable(expirationDate),
            claims = emptyMap(),
            location = location,
            difficulty = difficulty.toString(),
            likes = mapOf(),
        )

        // Get the reference to the thumbnail Bitmap and set the value
        val thumbnailBitmap = getChallengeThumbnailById(challengeId)
        thumbnailBitmap.value = thumbnail

        // Create both jobs (update database, update storage)
        val submitToDatabaseTask = dbChallengeRef.setValue(challengeEntry)
        val submitToStorageTask = thumbnailBitmap.saveToLocalStorageThenSubmit()

        // Finally make the completable task that succeed if both task succeeded
        return Tasks.whenAll(submitToDatabaseTask, submitToStorageTask).thenMap {
            FirebaseChallenge(
                cid = challengeId,
                author = getUserById(currentUser),
                thumbnail = thumbnailBitmap,
                publishedDate = localFromUtcIso8601(challengeEntry.publishedDate!!),
                expirationDate = expirationDate,
                correctLocation = location,
                claims = listOf(),
                description = description,
                difficulty = difficulty,
                likes = listOf(),
                numberOfActiveHunters = 0
            )
        }
    }

    override fun submitClaim(
        thumbnail: Bitmap,
        challenge: Challenge,
        location: Location
    ): Task<Claim> {
        require(thumbnail.width * thumbnail.height < R.integer.maximum_number_of_pixel_per_photo)

        // State variable
        val dbClaimRef = dbClaimRef.push()
        val claimId = dbClaimRef.key!!

        val claimEntry = ClaimEntry(
            user = currentUser,
            cid = challenge.cid,
            time = utcIso8601Now(),
            location = location
        )

        // Get the reference to the thumbnail Bitmap and set the value
        val thumbnailBitmap = getClaimThumbnailById(claimId)
        thumbnailBitmap.value = thumbnail

        // Create both jobs (update database, update storage)
        val submitToDatabaseTask = dbClaimRef.setValue(claimEntry)
        val submitToStorageTask = thumbnailBitmap.saveToLocalStorageThenSubmit()

        // Finally make the completable task that succeed if both task succeeded
        return Tasks.whenAll(submitToDatabaseTask, submitToStorageTask).thenMap {
            FirebaseClaim(
                id = claimId,
                user = getUserById(currentUser),
                time = localFromUtcIso8601(claimEntry.time!!),
                challenge = getChallengeById(challenge.cid),
                location = location,
                distance = 0,
                awardedPoints = 0,
                image = getClaimThumbnailById(claimId)
            )
        }
    }

    /**
     * Inserts a new user with empty information into the database.
     * This does not take into consideration profile picture, etc.
     * If the user already exists, it will override the user. Use with caution.
     */
    override fun insertNewUser(user: User): Task<Void> {
        val userEntry = UserEntry(user.displayName, mapOf(), mapOf(), score = 0)

        return dbUserRef.child(user.uid).setValue(userEntry)
    }

    /**
     * Point-Of-Interest user is a special user that does not have any information within the database
     *
     * It represent the user attach to any "public" challenge such as Point-Of-Interest. This design
     * choices was made to have a location where we can fetch easily "all" point of interest
     */
    override fun getPOIUserID(): String {
        return "0"
    }

    /**
     * Retrieve a challenge with a given ID and the corresponding [LazyRef]. Notice that this operation
     * won't fail if the given element does not exists in the database. The failure will happend upon
     * fetching the returned [LazyRef]
     *
     * @param cid the challenge unique identifier
     * @return A [LazyRef] linked to the result of the operation
     */
    override fun getChallengeById(cid: String): LazyRef<Challenge> {
        return challengeRefById.get(cid)
    }

    /**
     * Retrieve an image with a given ID and the corresponding [LazyRef]. Notice that this operation
     * won't fail if the given element does not exists in the database. The failure will happend upon
     * fetching the returned [LazyRef]
     *
     * @param iid the image id, this may depend for image type
     * @return A [LazyRef] linked to the result of the operation
     */
    override fun getImageById(iid: String): LazyRef<Bitmap> {
        return imageRefById.get(iid)
    }

    /**
     * Retrieve an User with a given ID and the corresponding [LazyRef]. Notice that this operation
     * won't fail if the given element does not exists in the database. The failure will happen upon
     * fetching the returned [LazyRef]
     *
     * @param uid the user id
     * @return A [LazyRef] linked to the result of the operation
     */
    override fun getUserById(uid: String): LiveLazyRef<User> {
        return if (uid == getPOIUserID()) {
            LiveLazyRef.fromLazyRef(FirebasePOIUserRef(uid))
        } else {
            userRefById.get(uid)
        }
    }

    /**
     * Updates the user with all the data
     */
    override fun updateUser(editedUser: EditedUser): Task<Void?> {
        val uid = editedUser.user.uid
        val userRef = dbUserRef.child(uid)
        val updateNameFieldTask =
            userRef.child("displayName").setValue(editedUser.displayName)

        if (editedUser.profilePicture != null) {
            val hash = BitmapUtils.hash(editedUser.profilePicture!!)
            val ppRef = getProfilePicture(uid, hash)
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

    override fun getClaimById(cid: String): LazyRef<Claim> {
        return claimRefById.get(cid)
    }

    internal fun getChallengeThumbnailById(cid: String) : FirebaseBitmapRef {
        return imageRefById.get(FirebaseBitmapRef.getImageIdFromChallengeId(cid))
    }

    internal fun getClaimThumbnailById(cid: String) : FirebaseBitmapRef {
        return imageRefById.get(FirebaseBitmapRef.getImageIdFromClaimId(cid))
    }

    internal fun getProfilePicture(uid: String, hash: Int): FirebaseBitmapRef {
        return imageRefById.get(FirebaseBitmapRef.getProfilePictureId(uid, hash.toString()))
    }

    @Deprecated("all getFooRefById should be replaced by getFooById")
    internal fun getClaimRefById(id: String) : LazyRef<Claim> {
        TODO()
    }

    /**
     * Get a list of nearby challenges to a specific location
     *
     * @param location the location we are interested in
     * @return [Task] a task completed once the operation succeeded (or failed successfully)
     */
    override fun getNearbyChallenge(location: Location): Task<List<Challenge>> {
        val listOfQuadrant = listOf("3cc359ec")

        val xs = listOfQuadrant.map { cid ->
            dbChallengeRef.child(cid).get()
                .thenMap {
                    val challenge = it.buildChallenge(this, cid)
                    challengeRefById.register(cid, FirebaseChallengeRef(cid, this, challenge))
                    challenge
                }
        }

        return Tasks.whenAllSuccess<Challenge>(xs)
            .thenMap { it.toImmutableList() }
    }

    override fun getFollowersOf(uid: String): Task<List<LazyRef<User>>> {
        return dbFollowersRef.child(uid)
            .get()
            .thenMap { snapshot ->
                snapshot.toMap<Boolean>()
                    .mapNotNull { (uid, exists) -> getUserById(uid).takeIf { exists } }
            }
    }

    override fun getTopNUsers(n: Int): Task<List<LazyRef<User>>> {
        return dbUserRef
            .orderByChild("score")
            .limitToLast(n)
            .get()
            .thenMap { dataSnapshot ->
                val resultingMap = dataSnapshot.convertTo<Map<String, UserEntry>>() ?: emptyMap<String, UserEntry>()
                resultingMap.mapNotNull { it }
                    .sortedByDescending { (_, entry) -> entry.score }
                    .map { (uid, _) -> getUserById(uid) }
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
    private suspend fun doFollow(follower: String, followee: String, follow: Boolean = true) {
        // TODO Writes are not made atomically and should be batched instead
        //      See https://github.com/SDP-GeoHunt/geo-hunt/issues/88#issue-1647852411

        val followerListRef = dbUserRef.child(follower).child("followList")
        val counterRef = dbUserRef.child(followee).child("numberOfFollowers")
        val follows = dbFollowersRef.child(followee)

        val defaultMap = emptyMap<String, Boolean>().withDefault { false }
        val followerList = followerListRef.queryAs<Map<String, Boolean>>() ?: defaultMap
        val followedCounter = counterRef.queryAs<Int>() ?: 0
        val followsPairs = follows.queryAs<Map<String, Boolean>>() ?: defaultMap

        // Abort if the user already follows the followee
        // or if the user tries to unfollow someone not followed
        if ((follow && followerList[followee] == true)
            || (!follow && followerList[followee] != true)) {
            return
        }

        Tasks.whenAll(
            // Update the follower's follow list
            followerListRef.setValue(if (follow) (followerList + (followee to true)) else followerList - followee),

            // Update the follow counter of the followee
            counterRef.setValue(if (follow) followedCounter + 1 else max(followedCounter - 1, 0)),

            // Update the follows relationship
            follows.setValue(if (follow) followsPairs + (follower to true) else followsPairs - follower)
        ).await()
    }

    private suspend fun doJoinHunt(cid: String, uid: String, doJoin: Boolean) {
        val activeHuntsListRef = dbUserRef.child(uid).child("activeHunts")
        val numberOfActiveHuntersRef = getChallengeRefFromId(cid).child("numberOfActiveHunters")

        val activeHuntsList = activeHuntsListRef.queryAs<Map<String, Boolean>>() ?: emptyMap<String, Boolean>().withDefault { false }
        val numberOfActiveHunters = numberOfActiveHuntersRef.queryAs<Int>() ?: 0


        if ((doJoin && activeHuntsList[cid] == true)
            || (!doJoin && activeHuntsList[cid] != true)) {
            return
        }

        Tasks.whenAll(
            // Update the active hunt list
            activeHuntsListRef.setValue(if (doJoin) (activeHuntsList + (cid to true)) else activeHuntsList - cid),

            // Update the number of followers counter
            numberOfActiveHuntersRef.setValue(if (doJoin) numberOfActiveHunters + 1 else max(numberOfActiveHunters - 1, 0))
        ).await()
    }

    /**
     * Makes the first user with the given UID follow the second user.
     */
    override suspend fun follow(follower: String, followee: String) {
        doFollow(follower, followee, follow = true)
    }

    override suspend fun unfollow(follower: String, followee: String) {
        doFollow(follower, followee, follow = false)
    }

    override suspend fun joinHunt(cid: String) {
        val uid = Authenticator.authInstance.get().user!!.uid
        doJoinHunt(cid, uid, doJoin = true)
    }

    override suspend fun leaveHunt(cid: String) {
        val uid = Authenticator.authInstance.get().user!!.uid
        doJoinHunt(cid, uid, doJoin = false)
    }

    /**
     * Returns a list of users that liked a given challenge
     * @param cid the challenge id
     * @return a list of users that liked the challenge
     */
    internal fun getLikesOf(cid: String): Task<Map<String, Boolean>> {
        val coarseHash = cid.substring(0, Location.COARSE_HASH_SIZE)
        val elementId = cid.substring(Location.COARSE_HASH_SIZE)

        return dbChallengeRef.child(coarseHash).child(elementId).child("likedBy").get()
            .thenMap { snapshot ->
                snapshot.toMap<Boolean>().withDefault { false }
            }
    }

    /**
     * Function updating the firebase making user like/dislike a given challenge
     *
     * @note To ensure easier data querying, the data must be synchronized at 3 places in the database :
     *      - In the user's like list, the challenge should be added/removed,
     *      - The challenge's counter should be incremented/decremented,
     *      - The user should be added/removed to the challenge's liker list.
     */
    private suspend fun doLike(uid: String, cid: String, like: Boolean = true) {
        // TODO Writes are not made atomically and should be batched instead
        //      See https://github.com/SDP-GeoHunt/geo-hunt/issues/88#issue-1647852411
        val coarseHash = cid.substring(0, Location.COARSE_HASH_SIZE)
        val elementId = cid.substring(Location.COARSE_HASH_SIZE)

        val userLikesRef = dbUserRef.child(uid).child("likes")
        val challengeLikesRef = dbChallengeRef.child(coarseHash).child(elementId).child("likedBy")

        val defaultMap = emptyMap<String, Boolean>().withDefault { false }
        val userLikes = userLikesRef.queryAs<Map<String, Boolean>>() ?: defaultMap
        val challengeLikes = challengeLikesRef.queryAs<Map<String, Boolean>>() ?: defaultMap

        // Abort if the user already likes the challenge
        // or if the user tries to unlike a challenge not liked
        if ((like && userLikes[cid] == true)
            || (!like && userLikes[cid] != true)) {
            return
        }

        Tasks.whenAll(
            // Update the user's likes list
            userLikesRef.setValue(if (like) (userLikes + (cid to true)) else userLikes - cid),

            // Update the challenge's likes list
            challengeLikesRef.setValue(if (like) (challengeLikes + (uid to true)) else challengeLikes - uid)
        ).await()
    }

    /**
     * Insert a like for a user into the Firebase
     * @param uid the user id
     * @param cid the challenge id
     * @return a task that will complete when the like is inserted
     */
    override suspend fun insertUserLike(uid: String, cid: String) {
        doLike(uid, cid, like = true)
    }

    /**
     * Remove a like for a user from the Firebase
     *
     * @param uid the user id
     * @param cid the challenge id
     * @return a task that will complete when the like is removed
     */
    override suspend fun removeUserLike(uid: String, cid: String) {
        doLike(uid, cid, like = false)
    }

    /**
     * Check if a user likes a specific challenge
     *
     * @param uid the user id
     * @param cid the challenge id
     * @return a task that will complete with a boolean indicating if the user likes the challenge
     */
    override fun doesUserLike(uid: String, cid: String): LazyRef<Boolean> {
        //Check if the challenge is in the user's liked challenges, return false if the challenge is not present
        return object : BaseLazyRef<Boolean>() {
            override fun fetchValue(): Task<Boolean> {
                return dbUserRef.child(uid).child("likes").get().thenMap {
                    it.child(uid).getValue(Boolean::class.java) ?: false
                }
            }
            override val id: String = uid + cid
        }
    }
}
