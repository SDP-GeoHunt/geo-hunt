package com.github.geohunt.app.data.repository

import com.github.geohunt.app.model.User

/**
 * The repository handling all the user's scores
 */
interface ScoreRepositoryInterface {

    /**
     * Get the score of the given user
     * @param user the user we want the score of
     */
    suspend fun getScore(user: User): Long {
        return getScore(user.id)
    }

    /**
     * Get the score of given uid
     * @param uid the user id we want the score of
     */
    suspend fun getScore(uid: String): Long

    /**
     * Increment the score of an user of some given amount
     * @param user the user that will see its score incremented
     * @param increment the amount we want to add to the users score
     */
    suspend fun incrementUserScore(user: User, increment: Long) {
        incrementUserScore(user.id, increment)
    }

    /**
     * Increment the score of an user of some given amount
     * @param uid the user id of the user that will see its score incremented
     * @param increment the amount we want to add to the users score
     */
    suspend fun incrementUserScore(uid: String, increment: Long)

    /**
     * Get the uids and scores of the n best players
     * The result is sorted in descending order
     * @param n the amount of users to get (at most)
     */
    suspend fun getTopNUsers(n: Int): List<Pair<String, Long>>
}