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
    override val activeHunts: List<LazyRef<Challenge>>,
    override val numberOfFollowers: Int,
    override val followList: List<LazyRef<User>>,
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
                uid = id,
                displayName = entry.displayName,
                profilePicture = db.getProfilePicture(id),
                challenges = entry.challenges.mapNotNull { (id, exists) -> db.getChallengeById(id).takeIf { exists } },
                activeHunts = entry.activeHunts.mapNotNull { (id, exists) -> db.getChallengeById(id).takeIf { exists } },
                numberOfFollowers = entry.numberOfFollowers,
                followList = entry.followList.mapNotNull { (id, doesFollow) -> db.getUserById(id).takeIf { doesFollow } },
                score = entry.score
            )
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
    var numberOfFollowers: Int = 0,
    var followList: Map<String, Boolean> = emptyMap(),
    var score: Long = 0
)
