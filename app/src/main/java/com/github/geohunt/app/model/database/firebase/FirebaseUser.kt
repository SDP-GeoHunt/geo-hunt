package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.api.UserNotFoundException
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task

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
    override var score: Long
) : User {

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
                follows = entry.follows.toList().filter { it.second }
                    .map { (id, _) -> db.getUserById(id) },
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
