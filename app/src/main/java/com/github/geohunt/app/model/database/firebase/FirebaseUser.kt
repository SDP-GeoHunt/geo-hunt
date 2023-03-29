package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.google.android.gms.tasks.Task

class FirebaseUser(
    override val uid: String,
    override var displayName: String?,
    override val profilePicture: LazyRef<Bitmap>,
    override val challenges: List<LazyRef<Challenge>>,
    override val hunts: List<LazyRef<Challenge>>,
    override val followers: Int,
    override val follows: Map<String, Boolean>,
    override var score: Number
) : User

class FirebaseUserRef(override val id: String) : BaseLazyRef<User>() {
    override fun fetchValue(): Task<User> {
        TODO("Not yet implemented")
    }
}


