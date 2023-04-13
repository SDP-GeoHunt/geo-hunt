package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.User
import com.google.android.gms.tasks.Task

abstract class MockLoggedUserContext : LoggedUserContext {
    override val loggedUser: LazyRef<User>
        get() = TODO("Not yet implemented")

    override val User.doesFollow: Task<Boolean>
        get() = TODO("Not yet implemented")

    override fun getFollowedUsers(): Task<List<LazyRef<User>>> {
        TODO("Not yet implemented")
    }

    override fun getFollowers(): Task<List<LazyRef<User>>> {
        TODO("Not yet implemented")
    }
}