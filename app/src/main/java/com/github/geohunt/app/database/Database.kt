package com.github.geohunt.app.database

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import com.github.geohunt.app.database.firebase.FirebaseDatabase
import com.github.geohunt.app.database.models.Challenge
import com.github.geohunt.app.database.models.ChallengeVisibility
import com.github.geohunt.app.database.models.Location
import java.util.concurrent.CompletableFuture


interface Database {
    fun createChallenge(bitmap: Bitmap, visibility: ChallengeVisibility, location: Location) : CompletableFuture<Challenge>

    fun getChallengeById(cid: String) : CompletableFuture<Challenge>

    fun getNearbyChallenge(location: Location) : CompletableFuture<List<Challenge>>

    // fun getProfileById(uid: String) : CompletableFuture<Profile>

}

/**
 * Utility class to create a new database handle (this is used for testing purpose
 * to replace the database instance with a mock one using mockito)
 */
object DatabaseFactory  {

    /**
     * Create a new instance of a database
     */
    fun createDatabaseHandle(activity: Activity) : Database {
        return FirebaseDatabase(activity)
    }
}

