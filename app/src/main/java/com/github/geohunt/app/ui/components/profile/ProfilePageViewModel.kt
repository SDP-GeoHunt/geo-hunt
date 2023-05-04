package com.github.geohunt.app.ui.components.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.exceptions.UserNotFoundException
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.database.api.ProfileVisibility
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class ProfilePageViewModel(
    private val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val challengeRepository: ChallengeRepositoryInterface,
    private val followRepository: FollowRepositoryInterface,
    private val profileVisibilityRepository: ProfileVisibilityRepositoryInterface,
    @Suppress("DEPRECATION") private val uid: String = authRepository.getCurrentUser().id,
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

    @Suppress("DEPRECATION")
    private val authedUid = authRepository.getCurrentUser().id
    val isSelf = authedUid == uid
    val canFollow = !isSelf

    private val _doesFollow = followRepository.doesFollow(uid)
    val doesFollow = _doesFollow.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private var _didFail = MutableStateFlow<Exception?>(null)
    val didFail = _didFail.asStateFlow()

    private var _isPrivate = MutableStateFlow<Boolean>(false)
    val isPrivate = _isPrivate.asStateFlow()

    private var isFollowTransactionDone = true // flag to prevent spamming the follow button
    fun follow() {
        if (isFollowTransactionDone && _user.value != null && !doesFollow.value) {
            isFollowTransactionDone = false
            viewModelScope.launch {
                followRepository.follow(_user.value!!)
                isFollowTransactionDone = true
            }
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

    /**
     * Refreshes the data
     */
    fun refresh() {
        viewModelScope.launch {
            if (!_isRefreshing.value) {
                _isRefreshing.value = true
                fetch()
                _isRefreshing.value = false
            }
        }
    }

    private fun fetch() {
        viewModelScope.launch {
            try {
                if (!isSelf) {
                    when (profileVisibilityRepository.getProfileVisibility(uid).first()) {
                        ProfileVisibility.PUBLIC -> {}
                        ProfileVisibility.PRIVATE -> { _isPrivate.value = true; return@launch }
                        ProfileVisibility.FOLLOWING_ONLY -> {
                            val isPrivate = !followRepository.doesFollow(uid, authedUid).first()
                            _isPrivate.value = isPrivate
                            if (isPrivate) return@launch
                        }
                    }
                }

                _user.value = userRepository.getUser(uid)
                _challenges.value = challengeRepository.getPosts(uid).first()
                _claims.value = challengeRepository.getClaimsFromUser(uid)
                _claimedChallenges.value = _claims.value!!.map { challengeRepository.getChallenge(it.parentChallengeId) }
            } catch (e: UserNotFoundException) {
                _didFail.value = e
            }
        }
    }

    fun toggleFollow() {
        viewModelScope.launch {
            if (!doesFollow.value) follow() else unfollow()
        }
    }

    init {
        fetch()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                ProfilePageViewModel(
                    container.auth,
                    container.user,
                    container.challenges,
                    container.follow,
                    container.profileVisibilities
                )
            }
        }
    }
}