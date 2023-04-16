package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.*
import com.google.android.gms.tasks.Task
import java.time.LocalDateTime

abstract class MockLoggedUserContext : LoggedUserContext {
    override val loggedUserRef: LazyRef<User>
        get() = TODO("Not yet implemented")

    override val User.doesFollow: LazyRef<Boolean>
        get() = TODO("Not yet implemented")

    override fun User.follow(): Task<Nothing?> {
        TODO("Not yet implemented")
    }

    override fun LazyRef<User>.follow(): Task<Nothing?> {
        TODO("Not yet implemented")
    }

    override fun User.unfollow(): Task<Nothing?> {
        TODO("Not yet implemented")
    }

    override fun LazyRef<User>.unfollow(): Task<Nothing?> {
        TODO("Not yet implemented")
    }

    override fun Challenge.joinHunt(): Task<Nothing?> {
        TODO("Not yet implemented")
    }

    override fun Challenge.leaveHunt(): Task<Nothing?> {
        TODO("Not yet implemented")
    }

    override fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        expirationDate: LocalDateTime?
    ): Task<Challenge> {
        TODO("Not yet implemented")
    }

    override fun Challenge.submitClaim(thumbnail: Bitmap, location: Location): Task<Claim> {
        TODO("Not yet implemented")
    }

    override fun getFollowedUsers(): Task<List<LazyRef<User>>> {
        TODO("Not yet implemented")
    }

    override fun getFollowers(): Task<List<LazyRef<User>>> {
        TODO("Not yet implemented")
    }
}