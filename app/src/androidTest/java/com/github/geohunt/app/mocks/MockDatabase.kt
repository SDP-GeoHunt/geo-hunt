package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Database
import com.github.geohunt.app.model.database.api.*
import com.google.android.gms.tasks.Task
import java.time.LocalDateTime

abstract class BaseMockDatabase : Database {
    override fun getChallengeById(cid: String): LazyRef<Challenge> {
        throw NotImplementedError()
    }

    override fun getImageById(iid: String): LazyRef<Bitmap> {
        TODO("Not yet implemented")
    }

    override fun getNearbyChallenge(location: Location): Task<List<Challenge>> {
        throw NotImplementedError()
    }

    override fun getLoggedContext(): LoggedUserContext {
        return object : MockLoggedUserContext() {}
    }

    override fun getFollowersOf(uid: String): Task<List<LazyRef<User>>> {
        TODO("Not yet implemented")
    }

    override fun insertNewUser(user: User): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun getUserById(uid: String): LazyRef<User> {
        TODO("Not yet implemented")
    }
}