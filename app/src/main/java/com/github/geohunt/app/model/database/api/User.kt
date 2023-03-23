package com.github.geohunt.app.model.database.api

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef

/**
 * Define the profile information of a user as stored in the database
 */
interface User {
    val uid: String

    var displayName: String?

    val name: String
        get() = displayName ?: ("@" + uid)

    val profilePicture: LazyRef<Bitmap>

    val challenges: List<LazyRef<Challenge>>

    val hunts: List<LazyRef<Challenge>>

    /**
     * The number of users that follow this user.
     */
    val followerNumber: Int

    /**
     * The list of users this user is following.
     */
    val followList: List<LazyRef<User>>

    var score: Number
}
