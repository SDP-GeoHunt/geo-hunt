package com.github.geohunt.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

open class BountyFeedViewModel(
    override val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val bountiesRepository: BountiesRepositoryInterface
): AuthViewModel(authRepository) {
    // Bounties
    private val _bountyList: MutableStateFlow<List<Bounty>?> = MutableStateFlow(null)
    open val bountyList = _bountyList.asStateFlow()

    // Bounties authors
    private val _bountyAuthors: MutableStateFlow<Map<String, User>> = MutableStateFlow(mapOf())
    val bountyAuthors = _bountyAuthors.asStateFlow()

    // Bounties challenges
    private val _bountyChallenges: MutableStateFlow<Map<String, List<Challenge>>> = MutableStateFlow(mapOf())
    val bountyChallenges = _bountyChallenges.asStateFlow()

    // Bounties teams number
    private val _nbParticipating: MutableStateFlow<Map<String, Int>> = MutableStateFlow(mapOf())
    val nbParticipating = _nbParticipating.asStateFlow()

    // Refreshing state
    private val _areBountiesRefreshing = MutableStateFlow(false)
    val areBountiesRefreshing = _areBountiesRefreshing.asStateFlow()

    // Is already inside
    private val _isAlreadyInsideBounties = MutableStateFlow<List<Bounty>>(listOf())
    val isAlreadyInsideBounties = _isAlreadyInsideBounties.asStateFlow()

    init {
        viewModelScope.launch { fetchBounties() }
    }

    open suspend fun fetchBounties() {
        val bountyList = bountiesRepository.getBounties()
            .filter {
                val userTeam = bountiesRepository.getTeamRepository(it).getUserTeam().first()
                val now = LocalDateTime.now()

                // Disgusting, but working!
                if (userTeam != null) { _isAlreadyInsideBounties.value += it }

                userTeam != null || (now.isAfter(it.startingDate) && now.isBefore(it.expirationDate))
            }

        // For each bounties, get the challenges to show them
        bountyList.forEach {

            // Fetch challenges
            _bountyChallenges.value = _bountyChallenges.value +
                    (it.bid to bountiesRepository.getChallengeRepository(it)
                        .getChallenges())

            // Fetch the number of people participating
            _nbParticipating.value = _nbParticipating.value +
                    (it.bid to bountiesRepository.getTeamRepository(it).getTeams().first()
                        .sumOf { it.membersUid.size })

            // Get author
            _bountyAuthors.value = _bountyAuthors.value +
                    (it.bid to userRepository.getUser(it.adminUid))

        }
        _bountyList.value = bountyList
    }

    open fun refreshBounties() {
    viewModelScope.launch {
            _areBountiesRefreshing.value = true
            fetchBounties()
            _areBountiesRefreshing.value = false
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                BountyFeedViewModel(
                    container.auth,
                    container.user,
                    container.bounty
                )
            }
        }
    }
}