package com.github.geohunt.app.authentication

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.github.geohunt.app.model.database.api.User

class FirebaseUserAdapter(firebaseUser: FirebaseUser) : User {
    private val user: FirebaseUser = firebaseUser

    @Deprecated("You should prefer getting the FirebaseUserRef from the Database directly.")
    override var displayName: String?
        get() = user.displayName
        set(value) {
            user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(value).build())
        }

    override val uid get() = user.uid

    @Deprecated("You should prefer getting the FirebaseUserRef from the Database directly.")
    override val profilePicture: LazyRef<Bitmap>
        get() = throw java.lang.UnsupportedOperationException()

    @Deprecated("You should prefer getting the FirebaseUserRef from the Database directly.")
    override val profilePictureHash: Int
        get() = throw java.lang.UnsupportedOperationException()


    @Deprecated("You should prefer getting the FirebaseUserRef from the Database directly.")
    override val challenges: List<LazyRef<Challenge>>
        get() = throw java.lang.UnsupportedOperationException()


    @Deprecated("You should prefer getting the FirebaseUserRef from the Database directly.")
    override val hunts: List<LazyRef<Challenge>>
        get() = throw java.lang.UnsupportedOperationException()


    @Deprecated("You should prefer getting the FirebaseUserRef from the Database directly.")
    override val numberOfFollowers: Int
        get() = throw java.lang.UnsupportedOperationException()


    @Deprecated("You should prefer getting the FirebaseUserRef from the Database directly.")
    override val follows: List<LazyRef<User>>
        get() = throw java.lang.UnsupportedOperationException()


    @Deprecated("You should prefer getting the FirebaseUserRef from the Database directly.")
    override var score: Long
        get() = throw java.lang.UnsupportedOperationException()
        set(value) { throw java.lang.UnsupportedOperationException() }

    @Deprecated("You should prefer getting the FirebaseUserRef from the Database directly.")
    override var likes: List<LazyRef<Challenge>>
        get() = TODO("Not yet implemented (need database)")
        set(value) {}

    override val isPOIUser : Boolean = false
}