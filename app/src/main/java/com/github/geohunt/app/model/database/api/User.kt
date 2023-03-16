package com.github.geohunt.app.model.database.api

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef

/**
 * Define the profile information of a user as stored in the database
 */
interface User {
    val uid: String

    var displayName: String

    val profilePicture: LazyRef<Bitmap>

    val challenges: List<LazyRef<Challenge>>

    val hunts: List<LazyRef<Challenge>>

    var score: Number
}
