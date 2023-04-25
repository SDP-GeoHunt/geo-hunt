package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.ProfileVisibility
import com.github.geohunt.app.model.database.api.User

class MockUser(
    override var displayName: String? = null,
    override val uid: String = "1",
    override val profilePicture: LazyRef<Bitmap> = MockProfilePicture,
    override val profilePictureHash: Int? = 1,
    override val challenges: List<LazyRef<Challenge>> = emptyList(),
    override val activeHunts: List<LazyRef<Challenge>> = emptyList(),
    override val numberOfFollowers: Int = 0,
    override val followList: List<LazyRef<User>> = emptyList(),
    override var score: Long = 1, override val isPOIUser: Boolean = false,
    override var likes: List<LazyRef<Challenge>> = emptyList(),
    override val rank: Int = 0,
    override val profileVisibility: ProfileVisibility = ProfileVisibility.PUBLIC
) : User
