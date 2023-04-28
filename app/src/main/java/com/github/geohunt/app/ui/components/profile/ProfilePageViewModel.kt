package com.github.geohunt.app.ui.components.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.data.repository.FollowRepository
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.maps.loadChallenges
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfilePageViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val challengeRepository: ChallengeRepository,
    private val followRepository: FollowRepository,
    uid: String = authRepository.getCurrentUser().id,
): ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _challenges = MutableStateFlow<List<Challenge>?>(null)
    val challenges = _challenges.asStateFlow()

    private val _claims = MutableStateFlow<List<Claim>?>(null)
    private val _claimedChallenges = MutableStateFlow<List<Challenge>?>(null)
    val claims = _claims.asStateFlow()
    val claimedChallenges = _claimedChallenges.asStateFlow()
    val score = _claims.asStateFlow().map { it?.sumOf { it.awardedPoints } }

    val canFollow = authRepository.getCurrentUser().id != uid

    private var _doesFollow = followRepository.doesFollow(uid)
    val doesFollow = _doesFollow.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private var _rank = MutableStateFlow<Int?>(null)
    val rank = _rank.asStateFlow()

    private var isFollowTransactionDone = true // flag to prevent spamming the follow button
    fun follow() {
        if (isFollowTransactionDone && _user.value != null && !doesFollow.value)
            isFollowTransactionDone = false
            viewModelScope.launch {
                followRepository.follow(_user.value!!)
                isFollowTransactionDone = true
            }
    }

    fun unfollow() {
        if (isFollowTransactionDone && _user.value != null && doesFollow.value) {
            isFollowTransactionDone = false
            viewModelScope.launch {
                followRepository.unfollow(_user.value!!)
                isFollowTransactionDone = true
            }
        }
    }

    init {
        viewModelScope.launch {
            _user.value = userRepository.getUser(uid)
            _challenges.value = challengeRepository.getChallengesFromUser(uid)
            _claims.value = challengeRepository.getClaimsFromUser(uid)
            _claimedChallenges.value = _claims.value!!.map { challengeRepository.getChallenge(it.parentChallengeId) }
        }
    }
}