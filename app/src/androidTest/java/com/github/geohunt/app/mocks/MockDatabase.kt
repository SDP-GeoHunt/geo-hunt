package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.*
import com.google.android.gms.tasks.Task
import java.time.LocalDateTime

abstract class BaseMockDatabase : Database {
    override fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        expirationDate: LocalDateTime?
    ): Task<Challenge> {
        throw NotImplementedError()
    }

    override fun getChallengeById(cid: String): LazyRef<Challenge> {
        throw NotImplementedError()
    }

    override fun getImageById(iid: String): LazyRef<Bitmap> {
        TODO("Not yet implemented")
    }

    override fun getNearbyChallenge(location: Location): Task<List<Challenge>> {
        throw NotImplementedError()
    }
    
    override fun submitClaim(
        thumbnail: Bitmap,
        challenge: Challenge,
        location: Location
    ): Task<Claim> {
        throw NotImplementedError()
}
    override fun getFollowersOf(uid: String): Task<Map<String, Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun follow(follower: String, followee: String) {
        TODO("Not yet implemented")
    }

    override suspend fun unfollow(follower: String, followee: String) {
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
}