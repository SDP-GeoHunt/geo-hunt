package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User

class MockUser(
    override var displayName: String? = null,
    override val uid: String = "1",
    override val profilePicture: LazyRef<Bitmap> = MockProfilePicture,
    override val challenges: List<LazyRef<Challenge>> = emptyList(),
    override val hunts: List<LazyRef<Challenge>> = emptyList(),
    override val numberOfFollowers: Int = 0,
    override val follows: List<LazyRef<User>> = listOf(),
    override var score: Long = 1
) : User