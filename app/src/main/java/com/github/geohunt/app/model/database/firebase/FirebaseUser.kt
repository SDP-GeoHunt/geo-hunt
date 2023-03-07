package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import java.util.concurrent.CompletableFuture

class FirebaseUser(
    override val uid: String,
    override var displayName: String?,
    override val profilePicture: LazyRef<Bitmap>,
    override val challenges: List<LazyRef<Challenge>>,
    override val hunts: List<LazyRef<Challenge>>,
    override var score: Number
) : User {

}

class FirebaseUserRef(override val id: String) : LazyRef<User> {
    override val value: User
        get() = TODO("Not yet implemented")

    override fun load(): CompletableFuture<User> {
        TODO("Not yet implemented")
    }
}


