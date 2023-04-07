package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.InvalidLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.api.UserNotFoundException
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase

/**
 * Internal implementation of User in the context of Firebase backend
 */
class FirebaseUser(
    override val uid: String,
    override val displayName: String?,
    override val profilePicture: LazyRef<Bitmap>,
    override val challenges: List<LazyRef<Challenge>>,
    override val hunts: List<LazyRef<Challenge>>,
    override val numberOfFollowers: Int,
    override val follows: List<LazyRef<User>>,
    override var score: Long,
    override val isPOIUser: Boolean = false,
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
            challenges = listOf(), //TODO: Implement challenges list-of as a LazyRef of list not the other way around
            hunts = listOf(),
            numberOfFollowers = 0,
            follows = listOf(),
            score = 0,
            isPOIUser = true
        ))
    }
}

/**
 * Internal implementation of the LazyRef for the firebase user
 */
internal class FirebaseUserRef(override val id: String, private val db: FirebaseDatabase) :
    BaseLazyRef<User>() {
    override fun fetchValue(): Task<User> {
        return db.dbUserRef.child(id).get().thenMap { dataSnapshot ->
            if (!dataSnapshot.exists()) {
                throw UserNotFoundException(id)
            }

            val entry = dataSnapshot.getValue(UserEntry::class.java)!!

            FirebaseUser(
                uid = entry.uid,
                displayName = entry.displayName,
                profilePicture = db.getProfilePicture(entry.uid),
                challenges = entry.challenges.map { db.getChallengeById(it) },
                hunts = entry.hunts.map { db.getChallengeById(it) },
                numberOfFollowers = entry.numberOfFollowers,
                follows = entry.follows.mapNotNull { (id, doesFollow) -> db.getUserById(id).takeIf { doesFollow } },
                score = entry.score
            )
        }
    }
}

/**
 * Raw user entry has stored in the database
 */
internal data class UserEntry(
    var uid: String = "",
    var displayName: String? = null,
    var challenges: List<String> = listOf(),
    var hunts: List<String> = listOf(),
    var numberOfFollowers: Int = 0,
    var follows: Map<String, Boolean> = emptyMap(),
    var score: Long = 0
)
