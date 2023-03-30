package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import java.time.LocalDateTime

class MockClaim(
    override val id: String,
    override val challenge: LazyRef<Challenge>,
    override val user: LazyRef<User>,
    override val time: LocalDateTime,
    override val location: Location) : Claim
