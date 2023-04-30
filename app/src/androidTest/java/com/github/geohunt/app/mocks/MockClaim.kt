package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import java.time.LocalDateTime

class MockClaim(
        override val id: String = "1",
        override val challenge: LazyRef<Challenge> = MockLazyRef("1") { TODO() },
        override val user: LazyRef<User> = MockLazyRef("1") { TODO() },
        override val time: LocalDateTime = LocalDateTime.now(),
        override val location: Location = Location(0.0, 0.0),
        override val image: LazyRef<Bitmap> = MockLazyRef("1") { TODO() },
        override val distance: Long = 0L,
        override val awardedPoints: Long = 0L
) : Claim
