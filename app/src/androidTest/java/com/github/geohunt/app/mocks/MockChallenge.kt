package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import java.time.LocalDateTime

class MockChallenge(
    override val cid: String,
    override val author: LazyRef<User>,
    override val publishedDate: LocalDateTime,
    override val expirationDate: LocalDateTime?,
    override val thumbnail: LazyRef<Bitmap>,
    override val correctLocation: Location,
    override val claims: List<LazyRef<Claim>>
) : Challenge {

    override val coarseLocation: Location
        get() = correctLocation.getCoarseLocation()

}