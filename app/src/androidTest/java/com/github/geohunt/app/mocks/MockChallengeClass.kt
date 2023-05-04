package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import java.time.LocalDateTime

// Keep this class until everything has been migrated
class MockChallengeClass(
    override val cid: String = "1",
    override val author: LazyRef<User> = MockLazyRef("1") { TODO() },
    override val publishedDate: LocalDateTime = LocalDateTime.now(),
    override val expirationDate: LocalDateTime? = null,
    override val thumbnail: LazyRef<Bitmap> = MockLazyRef("1") { TODO() },
    override val correctLocation: Location = Location(.0, .0),
    override val claims: List<LazyRef<Claim>> = listOf(),
    override val description: String? = null,
    override val difficulty: com.github.geohunt.app.model.database.api.Challenge.Difficulty =
        com.github.geohunt.app.model.database.api.Challenge.Difficulty.MEDIUM,
    override var likes: List<LazyRef<User>> = listOf(),
    override val numberOfActiveHunters: Int = 0
) : com.github.geohunt.app.model.database.api.Challenge {

    override val coarseLocation: Location
        get() = correctLocation.getCoarseLocation()

}