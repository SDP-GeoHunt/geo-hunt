package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
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

    override fun getNearbyChallenge(location: Location): Task<List<Challenge>> {
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
}