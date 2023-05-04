package com.github.geohunt.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.domain.GetUserFeedUseCase
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    override val authRepository: AuthRepository,
    val getUserFeedUseCase: GetUserFeedUseCase,
    val challengeRepository: ChallengeRepository
): AuthViewModel(authRepository) {
    private val _challengeFeed: MutableStateFlow<List<Challenge>?> = MutableStateFlow(null)
    val challengeFeed: StateFlow<List<Challenge>?> = _challengeFeed.asStateFlow()

    private val authorCache: MutableMap<Challenge, MutableStateFlow<User?>> = mutableMapOf()

    init {
        fetchChallengeFeed()
    }

    private fun fetchChallengeFeed() {
        viewModelScope.launch {
            /*val followList = getUserFeedUseCase.getFollowList()

            getUserFeedUseCase.getFollowFeed(followList).collect {
                _challengeFeed.value = it
            }*/

            getUserFeedUseCase.getDiscoverFeed(Location(46.51958, 6.56398)).collect {
                _challengeFeed.value = it
            }
        }
    }

    fun getChallengePhoto(challenge: Challenge): String = challengeRepository.getChallengePhoto(challenge)

    fun getAuthor(challenge: Challenge): StateFlow<User?> {
        if (!authorCache.contains(challenge)) {
            authorCache[challenge] = MutableStateFlow(null)
            viewModelScope.launch {
                authorCache[challenge]!!.value = challengeRepository.getAuthor(challenge)
            }
        }

        return authorCache[challenge]!!.asStateFlow()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                HomeViewModel(
                    container.auth,
                    container.feedUseCase,
                    container.challenges
                )
            }
        }
    }
}