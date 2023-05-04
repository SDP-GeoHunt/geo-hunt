package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.time.LocalDateTime

open class BaseMockDatabase : Database {
    override fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        difficulty: Challenge.Difficulty,
        expirationDate: LocalDateTime?,
        description: String?
    ): Task<Challenge> {
        TODO("Not yet implemented")
    }

    override fun getClaimById(cid: String): LazyRef<Claim> {
        TODO("Not yet implemented")
    }

    override fun getChallengeById(cid: String): LazyRef<Challenge> {
        throw NotImplementedError()
    }

    override fun getImageById(iid: String): LazyRef<Bitmap> {
        TODO("Not yet implemented")
    }

    override fun getNearbyChallenge(location: Location): Task<List<Challenge>> {
        TODO("Not yet implemented")
    }

    override fun getPOIUserID(): String {
        TODO("Not yet implemented")
    }

    override fun submitClaim(
        thumbnail: Bitmap,
        challenge: Challenge,
        location: Location
    ): Task<Claim> {
        throw NotImplementedError()
}
    override fun getFollowersOf(uid: String): Task<List<LazyRef<User>>> {
        TODO("Not yet implemented")
    }

    override fun getTopNUsers(n: Int): Task<List<LazyRef<User>>> {
        TODO("Not yet implemented")
    }

    override suspend fun follow(follower: String, followee: String) {
        TODO("Not yet implemented")
    }

    override suspend fun unfollow(follower: String, followee: String) {
        TODO("Not yet implemented")
    }

    override suspend fun joinHunt(cid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun leaveHunt(cid: String) {
        TODO("Not yet implemented")
    }

    override fun insertNewUser(user: User): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun getUserById(uid: String): LiveLazyRef<User> {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: EditedUser): Task<Void?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertUserLike(uid: String, cid: String) {
        //TODO("Not yet implemented")
    }

    override suspend fun removeUserLike(uid: String, cid: String) {
        //TODO("Not yet implemented")
    }

    override fun doesUserLike(uid: String, cid: String): LazyRef<Boolean> {
        //Return a lazy ref with the value true
        return object : LazyRef<Boolean> {
            override val id = "1101"
            override val value = false

            override fun fetch(): Task<Boolean> = Tasks.forResult(false)
        }
    }
}