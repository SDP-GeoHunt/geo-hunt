package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.google.android.gms.tasks.Task
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

abstract class BaseMockDatabase : Database {
    override fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        expirationDate: LocalDateTime?
    ): Task<Challenge> {
        throw NotImplementedError()
    }

    override fun getChallengeById(cid: String): Task<Challenge> {
        throw NotImplementedError()
    }

    override fun getNearbyChallenge(location: Location): Task<List<Challenge>> {
        throw NotImplementedError()
    }
}