package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import java.time.LocalDateTime

class MockChallenge(
    override val cid: String = "1",
    override val author: LazyRef<User> = MockLazyRef("1") { TODO() },
    override val publishedDate: LocalDateTime = LocalDateTime.now(),
    override val expirationDate: LocalDateTime? = null,
    override val thumbnail: LazyRef<Bitmap> = MockLazyRef("1") { TODO() },
    override val correctLocation: Location = Location(.0, .0),
    override val claims: List<LazyRef<Claim>> = listOf(),
    override var likes: List<LazyRef<User>> = listOf(),
    override var nbLikes: Int = 0
) : Challenge {

    override val coarseLocation: Location
        get() = correctLocation.getCoarseLocation()

}