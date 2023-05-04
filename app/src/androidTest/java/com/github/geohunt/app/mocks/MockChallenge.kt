package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.database.api.Location
import java.time.LocalDateTime

fun MockChallenge(
    id: String = "1",
    authorId: String = "1",
    photoUrl: String = "",
    location: Location = Location(0.0, 0.0),
    publishedDate: LocalDateTime = LocalDateTime.now(),
    expirationDate: LocalDateTime? = null,
    difficulty: Challenge.Difficulty = Challenge.Difficulty.MEDIUM,
    description: String? = null
) = Challenge(
    id = id,
    authorId = authorId,
    photoUrl = photoUrl,
    location = location,
    publishedDate = publishedDate,
    expirationDate = expirationDate,
    difficulty = difficulty,
    description = description
)