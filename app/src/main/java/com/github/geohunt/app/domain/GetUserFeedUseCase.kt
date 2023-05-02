package com.github.geohunt.app.domain

import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.data.repository.FollowRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.utility.aggregateFlows
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Combines the data from several repositories to create a personalized user feed.
 *
 * The user has the option to either show only the posts from followed users, or have a global
 * feed to discover locations around him.
 */
class GetUserFeedUseCase(
    val authRepository: AuthRepository,
    val challengeRepository: ChallengeRepository,
    val followRepository: FollowRepository
) {
    suspend fun getFollowList(): List<String> = followRepository.getFollowList().first()

    /**
     * Returns the personal feed. Publications in this feed will only come from users that are
     * followed.
     *
     * @param followList The current follow list.
     */
    fun getFollowFeed(followList: List<String>): Flow<List<Challenge>> {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        // Fetch all recent posts from the followed users
        val posts = followList.map { userId ->
            challengeRepository.getPosts(userId)
        }

        // Combine recent posts to make the feed
        return posts.aggregateFlows()
            .map { it.sortedByDescending { challenge -> challenge.publishedDate } }
    }

    /**
     * Returns a local feed where publications come from nearby posts.
     *
     * @param location The user current location.
     */
    fun getDiscoverFeed(location: Location): Flow<List<Challenge>> {
        // Get all sectors in a 10km radius
        val nearbySectors = location.getNeighboringSectors(10.0)

        return nearbySectors.map { challengeRepository.getSectorChallenges(it) }.aggregateFlows()
    }
}