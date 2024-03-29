package com.github.geohunt.app.ui.screens.activehunts

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.ActiveHuntsRepositoryInterface
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.ActiveBountiesRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class ActiveHuntsViewModel(
    override val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val activeHuntsRepository: ActiveHuntsRepositoryInterface,
    private val challengeRepository: ChallengeRepositoryInterface,
    activeBountiesRepository: ActiveBountiesRepositoryInterface,
    bountiesRepository: BountiesRepositoryInterface
): AuthViewModel(authRepository) {
    // The list of active hunts, where a null value indicates that the data is not fetched yet
    private val _activeHunts: MutableStateFlow<List<Challenge>?> = MutableStateFlow(null)
    open val activeHunts: StateFlow<List<Challenge>?> = _activeHunts.asStateFlow()

    private val _activeBounties: MutableStateFlow<List<Pair<Bounty, Challenge>>?> = MutableStateFlow(null)
    open val activeBounties: StateFlow<List<Pair<Bounty, Challenge>>?> = _activeBounties.asStateFlow()

    // The author names of the challenges.
    private val authorNames: MutableMap<Challenge, MutableStateFlow<String>> = mutableMapOf()

    init {
        viewModelScope.launch {
            activeHuntsRepository.getActiveHunts().map {
                it.map { id -> challengeRepository.getChallenge(id) }
            }.collect {
                _activeHunts.value = it
            }
        }

        viewModelScope.launch {
            activeBountiesRepository.getBounties().map {
                it.map { bid ->
                    Pair(
                        bountiesRepository.getBountyById(bid),
                        bountiesRepository.getChallengeRepository(bid).getChallenges().first()
                    )}
            }.collect {
                _activeBounties.value = it
            }
        }
    }

    /**
     * Returns the author name of the given challenge.
     *
     * To avoid fetching names multiple times when scrolling, names are cached for the duration of
     * the screen.
     */
    open fun getAuthorName(challenge: Challenge): StateFlow<String> {
        if (!authorNames.contains(challenge)) {
            authorNames[challenge] = MutableStateFlow("Loading …")
            viewModelScope.launch {
                authorNames[challenge]!!.value = userRepository.getUser(challenge.authorId).displayName!!
            }
        }
        return authorNames[challenge]!!.asStateFlow()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                ActiveHuntsViewModel(
                    container.auth,
                    container.user,
                    container.activeHunts,
                    container.challenges,
                    container.activeBounties,
                    container.bounty
                )
            }
        }
    }
}