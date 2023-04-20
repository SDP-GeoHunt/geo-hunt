package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.api.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

abstract class BaseMockDatabase : Database {
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

    override fun getLoggedContext(): LoggedUserContext {
        return object : MockLoggedUserContext() {}
    }

    override fun getFollowersOf(uid: String): Task<List<LazyRef<User>>> {
        TODO("Not yet implemented")
    }

    override fun getTopNUsers(n: Int): Task<List<LazyRef<User>>> {
        TODO("Not yet implemented")
    }

    override fun insertNewUser(user: User): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun getUserById(uid: String): LiveLazyRef<User> {
        TODO("Not yet implemented")
    }

    override fun getClaimById(iid: String): LazyRef<Claim> {
        TODO("Not yet implemented")
    }

}