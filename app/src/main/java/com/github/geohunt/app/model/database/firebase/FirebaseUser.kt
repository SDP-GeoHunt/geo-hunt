package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.*
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.api.UserNotFoundException
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * Internal implementation of User in the context of Firebase backend
 */
class FirebaseUser(
    override val uid: String,
    override val displayName: String?,
    override val profilePicture: LazyRef<Bitmap>?,
    override val profilePictureHash: Int?,
    override val challenges: List<LazyRef<Challenge>>,
    override val activeHunts: List<LazyRef<Challenge>>,
    override val numberOfFollowers: Int,
    override var score: Long,
    override val followList: List<LazyRef<User>>,
    override var likes: List<LazyRef<Challenge>>, override val isPOIUser: Boolean,
    override val rank: Int = 0,
) : User {
}

/**
 * Internal implementation of the LazyRef for the firebase user POI-User specifically
 */
internal class FirebasePOIUserRef(override val id: String) :
    BaseLazyRef<User>() {
    override fun fetchValue(): Task<User> {
        return Tasks.forResult(FirebaseUser(
            uid = id,
            displayName = null,
            profilePicture = InvalidLazyRef(RuntimeException("Cannot fetch profile picture for POI User")),
            challenges = listOf(),
            activeHunts = listOf(),
            likes = listOf(),
            followList = listOf(),
            numberOfFollowers = 0,
            score = 0,
            isPOIUser = true,
            profilePictureHash = null
        ))
    }
}

/**
 * Internal implementation of the LazyRef for the firebase user
 */
internal class FirebaseUserRef(override val id: String, private val db: FirebaseDatabase) :
    LiveLazyRef<User>() {

    private val child = db.dbUserRef.child(id)

    override fun addListener(callback: (User) -> Any?): LiveLazyRefListener {
        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entry = snapshot.getValue(UserEntry::class.java)
                value = fromEntryToUser(entry!!)
                callback(value!!)
            }

            override fun onCancelled(error: DatabaseError) { }
        }
        child.addValueEventListener(listener)
        return object: LiveLazyRefListener() {
            override fun stop() {
                child.removeEventListener(listener)
            }
        }
    }

    private fun fromEntryToUser(entry: UserEntry): User {
        return FirebaseUser(
            uid = id,
            displayName = entry.displayName,
            profilePicture = entry.profilePictureHash?.let { db.getProfilePicture(id, it) },
            profilePictureHash = entry.profilePictureHash,
            challenges = entry.challenges.mapNotNull { (cid, exists) -> db.getChallengeById(cid).takeIf { exists } },
            activeHunts = entry.activeHunts.mapNotNull { (cid, exists) -> db.getChallengeById(cid).takeIf { exists } },
            followList = entry.followList.mapNotNull { (id, exists) -> db.getUserById(id).takeIf { exists } },
            likes = entry.likes.mapNotNull { (id, exists) -> db.getChallengeById(id).takeIf { exists } },
            numberOfFollowers = entry.numberOfFollowers,
            score = entry.score,
            isPOIUser = false,
            rank = entry.rank
        )
    }

    override fun fetchValue(): Task<User> {
        return child.get().thenMap { dataSnapshot ->
            if (!dataSnapshot.exists()) {
                throw UserNotFoundException(id)
            }

            val entry = dataSnapshot.getValue(UserEntry::class.java)!!

            fromEntryToUser(entry)
        }
    }
}

/**
 * Raw user entry has stored in the database
 */
internal data class UserEntry(
    var displayName: String? = null,
    var challenges: Map<String, Boolean> = mapOf<String, Boolean>().withDefault { false },
    var activeHunts: Map<String, Boolean> = mapOf<String, Boolean>().withDefault { false },
    var likes: Map<String, Boolean> = mapOf<String, Boolean>().withDefault { false },
    var numberOfFollowers: Int = 0,
    var followList: Map<String, Boolean> = emptyMap(),
    var score: Long = 0,
    var rank: Int = 999,
    var profilePictureHash: Int? = null,
)
