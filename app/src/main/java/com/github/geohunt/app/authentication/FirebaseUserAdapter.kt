package com.github.geohunt.app.authentication

import com.github.geohunt.app.model.database.api.PictureImage
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

    override val uid get() = user.uid
    override val profilePicture: PictureImage?
        get() = null // TODO: needs to be get by the database

    override val challenges: List<String>
        get() = TODO("Not yet implemented")
    override val hunts: List<String>
        get() = TODO("Not yet implemented")
    override var score: Number
        get() = TODO("Not yet implemented")
        set(value) {}
}