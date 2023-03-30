package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
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

    override fun insertNewUser(user: User): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun getUser(uid: String): LazyRef<User> {
        TODO("Not yet implemented")
    }
}