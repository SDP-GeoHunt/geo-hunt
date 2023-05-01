package com.github.geohunt.app.ui.components.challenge

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChallengeViewModel(
    private val challengeRepository: ChallengeRepository,
    private val claimRepository: ClaimRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val followRepository: FollowRepository,
    private val activeHuntsRepository: ActiveHuntsRepository,
) : ViewModel() {

    data class State(
        val challenge: Challenge,
        val author: User,
        val claims: List<Claim>,
        val isSelf: Boolean,
        val authorScore: Long,
        val doesFollow: Flow<Boolean>,
        val doesHunt: Flow<Boolean>,
        val alreadyClaimed: Boolean
    )

    private val state_ : MutableStateFlow<State?> = MutableStateFlow(null)
    val state : StateFlow<State?> = state_

    fun follow() {
        require(state.value != null)
        viewModelScope.launch {
            followRepository.follow(state.value!!.author)
        }
    }

    fun unfollow() {
        require(state.value != null)
        viewModelScope.launch {
            followRepository.unfollow(state.value!!.author)
        }
    }

    fun joinHunt() {
        require(state.value != null)
        viewModelScope.launch {
            activeHuntsRepository.joinHunt(state.value!!.challenge)
        }
    }

    fun leaveHunt() {
        require(state.value != null)
        viewModelScope.launch {
            activeHuntsRepository.leaveHunt(state.value!!.challenge)
        }
    }

    fun retrieveUser(uid: String) : StateFlow<User?> {
        val mutableStateFlow = MutableStateFlow<User?>(null)

        viewModelScope.launch {
            mutableStateFlow.value = userRepository.getUser(uid)
        }

        return mutableStateFlow
    }

    fun withChallengeId(cid: String) {
        authRepository.requireLoggedIn()
        state_.value = null

        val currentUser = authRepository.getCurrentUser()

        viewModelScope.launch {
            val challenge = challengeRepository.getChallenge(cid)
            val userAsync = async { userRepository.getUser(challenge.authorId) }
            val claims = async { claimRepository.getClaimsByChallenge(challenge) }
            val user = userAsync.await()

            val authorScore = claimRepository.getScoreFromUser(user)

            state_.value = State(
                challenge = challenge,
                author = user,
                claims = claims.await(),
                isSelf = currentUser.id == user.id,
                authorScore = authorScore,
                doesFollow = followRepository.doesFollow(user),
                doesHunt = activeHuntsRepository.getDoesHunts(challenge),
                alreadyClaimed = claimRepository.doesClaims(challenge))
        }
    }

    companion object {
        fun createOf(appContainer: AppContainer) =
            ChallengeViewModel(
                appContainer.challenges,
                appContainer.claims,
                appContainer.user,
                appContainer.auth,
                appContainer.follow,
                appContainer.activeHunts)

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)
                createOf(container)
            }
        }
    }
}

