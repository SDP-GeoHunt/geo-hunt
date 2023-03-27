package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task

class FirebaseUser(
    override val uid: String,
    override var displayName: String?,
    override val profilePicture: LazyRef<Bitmap>,
    override val challenges: List<LazyRef<Challenge>>,
    override val hunts: List<LazyRef<Challenge>>,
    override var score: Number,
    override var likedChallenges: List<LazyRef<Challenge>>
) : User {
}

data class NotFoundUser(val id: String): Exception("User $id not found.")

class FirebaseUserRef(override val id: String, private val db: FirebaseDatabase) : BaseLazyRef<User>() {
    override fun fetchValue(): Task<User> {
        return db.dbUserRef.child(id).get().thenMap { data ->
            if (!data.exists()) {
                throw NotFoundUser(id)
            }

            val entry = data.getValue(UserEntry::class.java)!!

            FirebaseUser(
                uid = entry.uid,
                displayName = entry.displayName,
                profilePicture = db.getProfilePicture(entry.uid),
                challenges = entry.challenges.map { db.getChallengeById(it) },
                hunts = entry.hunts.map { db.getChallengeById(it) },
                score = entry.score,
                likedChallenges = entry.likedChallenges.map { db.getChallengeById(it) }
            )
        }
    }

    fun likeChallenge(cid: String): Task<Void> {
        return db.dbUserRef.child(id).child("likedChallenges").child(cid).setValue(true)
    }

    fun unlikeChallenge(cid: String): Task<Void> {
        return db.dbUserRef.child(id).child("likedChallenges").child(cid).removeValue()
    }
}

internal data class UserEntry(
    var uid: String = "",
    var displayName: String? = null,
    var challenges: List<String> = listOf(),
    var hunts: List<String> = listOf(),
    var score: Int = 0,
    var likedChallenges: List<String> = listOf()
)