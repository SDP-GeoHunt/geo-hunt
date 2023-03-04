package com.github.geohunt.app.database.models

import com.github.geohunt.app.utility.fromIso6801
import com.github.geohunt.app.utility.toIso8601
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import kotlin.streams.asSequence

data class Challenge(val challengeId: String,
                     val userId: String,
                     val picture: Picture,
                     val published: Instant,
                     val isPublic: Boolean,
                     val location: Location)
{
    /**
     * Data class representing challenge as they are described in the database
     */
    private data class ChallengeRaw(val userId: String ?= null,
                                    val pictureRaw: String ?= null,
                                    val published: String ?= null,
                                    val isPublic: Boolean ?= null,
                                    val location: Location ?= null)

    /**
     * Writes the given ChallengeRaw object to the provided DatabaseReference object.
     * @param dbRef the DatabaseReference object to write the data to
     * @return a Task object representing the write operation. The Task object can be used to
     *  monitor the status of the write operation.
     */
    fun writeToDatabase(dbRef: DatabaseReference) : Task<Void?> {
        val challengeRaw = ChallengeRaw(userId,
                Picture.serialize(picture),
                toIso8601(published),
                isPublic,
                location)
        return dbRef.setValue(challengeRaw)
    }

    companion object {
        fun fromDataSnapshot(challengeId: String, dataSnapshot: DataSnapshot) : Challenge {
            val challengeRaw = dataSnapshot.getValue(ChallengeRaw::class.java)!!
            return Challenge(challengeId,
                    challengeRaw.userId!!,
                    Picture.deserialize(challengeRaw.pictureRaw!!),
                    fromIso6801(challengeRaw.published!!),
                    challengeRaw.isPublic!!,
                    challengeRaw.location!!)
        }

        fun generateNewChallengeID(location: Location) : String {
            val charPool = ('a' .. 'z') + ('0' .. '9')
            val stringId = (1 .. 12)
                    .map { Random.nextInt(0, charPool.size) }
                    .asSequence()
                    .map(charPool::get)
                    .joinToString("")


            return Location.getCoarseHash(location) + stringId
        }
    }
}
