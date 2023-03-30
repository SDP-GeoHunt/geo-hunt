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
    override var score: Number
) : User {

}

data class NotFoundUser(val id: String): Exception("User $id not found.")

class FirebaseUserRef(override val id: String, private val db: FirebaseDatabase) : BaseLazyRef<User>() {
    override fun fetchValue(): Task<User> {
        return db.dbUserRef.child(id).get().thenMap {
            if (!it.exists()) {
                throw NotFoundUser(id)
            }

            val entry = it.getValue(UserEntry::class.java)!!

            FirebaseUser(
                uid = entry.uid,
                displayName = entry.displayName,
                profilePicture = db.getProfilePicture(entry.uid),
                challenges = entry.challenges.map { db.getChallengeRefById(it) },
                hunts = entry.hunts.map { db.getChallengeRefById(it) },
                score = entry.score
            )
        }
    }
}

internal data class UserEntry(
    var uid: String = "",
    var displayName: String? = null,
    var challenges: List<String> = listOf(),
    var hunts: List<String> = listOf(),
    var score: Int = 0
)
