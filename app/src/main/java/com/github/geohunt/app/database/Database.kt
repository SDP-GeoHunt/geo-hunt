package com.github.geohunt.app.database

import com.github.geohunt.app.database.models.Challenge
import com.github.geohunt.app.database.models.Location
import com.github.geohunt.app.database.models.Picture
import com.github.geohunt.app.utility.map
import com.github.geohunt.app.utility.serverNow
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Database {
    private val database = Firebase.database.reference
    private val currentUser : String = "8b8b0392-ba8b-11ed-afa1-0242ac120002"

    fun createChallenge(picture: Picture, isPublic: Boolean, location: Location) : Task<Challenge> {
        val coarseHash = Location.getCoarseHash(location)
        val newChallenge = database.child("challenges").child(coarseHash).push()
        val challengeId = newChallenge.key!!

        val challenge = Challenge(
                challengeId = challengeId,
                userId = currentUser,
                picture = picture,
                published = serverNow(),
                isPublic = isPublic,
                location = location
        )

        return challenge.writeToDatabase(newChallenge)
                .map {
                    challenge
                }
    }
}