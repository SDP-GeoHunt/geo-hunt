package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Location
import java.time.LocalDateTime

fun MockBounty(
        bid: String = "1",
        name: String = "Nice bounty",
        adminUid: String = "1",
        startingDate: LocalDateTime = LocalDateTime.MIN,
        expirationDate: LocalDateTime = LocalDateTime.MAX,
        location: Location = Location(0.0, 0.0)
) = Bounty(
        bid,
        name,
        adminUid,
        startingDate,
        expirationDate,
        location
)