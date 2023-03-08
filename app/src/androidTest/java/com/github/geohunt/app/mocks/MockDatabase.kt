package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

abstract class BaseMockDatabase : Database {
    override fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        expirationDate: LocalDateTime?
    ): CompletableFuture<Challenge> {
        throw NotImplementedError()
    }

    override fun getChallengeById(cid: String): CompletableFuture<Challenge> {
        throw NotImplementedError()
    }

    override fun getNearbyChallenge(location: Location): CompletableFuture<List<Challenge>> {
        throw NotImplementedError()
    }
}