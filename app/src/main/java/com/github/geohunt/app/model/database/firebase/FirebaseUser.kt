package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.InvalidLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.LiveLazyRefListener
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.api.UserNotFoundException
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.tasks.Tasks

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
    override val followList: List<LazyRef<User>>,
    override var likes: List<LazyRef<Challenge>>,
    override var score: Long,
    override val isPOIUser: Boolean = false,
    override val preferredLocale: String?,
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
            numberOfFollowers = 0,
            followList = listOf(),
            score = 0,
            isPOIUser = true,
            likes = listOf(),
            profilePictureHash = null,
            preferredLocale = null
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
                value = fromEntryToUser(snapshot.getValue(UserEntry::class.java)!!)
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
            numberOfFollowers = entry.numberOfFollowers,
            followList = entry.followList.mapNotNull { (id, doesFollow) -> db.getUserById(id).takeIf { doesFollow } },
            score = entry.score,
            likes = entry.likes.mapNotNull { (id, doesLike) -> db.getChallengeById(id).takeIf { doesLike } },
            preferredLocale = entry.preferredLocale
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
    var challenges: Map<String, Boolean> = emptyMap(),
    var activeHunts: Map<String, Boolean> = emptyMap(),
    var numberOfFollowers: Int = 0,
    var followList: Map<String, Boolean> = emptyMap(),
    var score: Long = 0,
    var profilePictureHash: Int? = null,
    var likes: Map<String, Boolean> = emptyMap(),
    var preferredLocale: String? = null
)
