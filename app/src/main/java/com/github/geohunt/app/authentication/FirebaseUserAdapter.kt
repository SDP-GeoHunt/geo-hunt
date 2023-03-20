package com.github.geohunt.app.authentication

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.github.geohunt.app.model.database.api.User

class FirebaseUserAdapter(firebaseUser: FirebaseUser) : User {
    private val user: FirebaseUser = firebaseUser

    override var displayName: String?
        get() = user.displayName
        set(value) {
            user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(value).build())
        }

    private val db = Database.databaseFactory.get()

    override val uid get() = user.uid
    override val profilePicture: LazyRef<Bitmap>
        get() = TODO("Not yet implemented (need database)")

    override val challenges: List<LazyRef<Challenge>>
        get() = TODO("Not yet implemented (need database)")
    override val hunts: List<LazyRef<Challenge>>
        get() = TODO("Not yet implemented (need database)")
    override var score: Double
        get() = TODO("Not yet implemented (need database)")
        set(value) {}
}