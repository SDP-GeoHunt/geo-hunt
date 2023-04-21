package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User

class MockUser(
    override var displayName: String? = null,
    override val uid: String = "1",
    override val profilePicture: LazyRef<Bitmap> = MockProfilePicture,
    override val profilePictureHash: Int = 0,
    override val challenges: List<LazyRef<Challenge>> = emptyList(),
    override val activeHunts: List<LazyRef<Challenge>> = emptyList(),
    override val numberOfFollowers: Int = 0,
    override val followList: List<LazyRef<User>> = listOf(),
    override var score: Long = 1,
    override var likes: List<LazyRef<Challenge>> = listOf(),
    override val isPOIUser: Boolean = false,
    override val rank: Int = 0
) : User