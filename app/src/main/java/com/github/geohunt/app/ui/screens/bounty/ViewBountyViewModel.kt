package com.github.geohunt.app.ui.screens.bounty

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * The view bounty page view model
 *
 * @param bountyId the bounty id
 * @param challengeRepository The challenge repository of the bounty
 * @param teamsRepository The team repository of the bounty
 */
class ViewBountyViewModel(
    bountyId: String,
    challengeRepository: ChallengeRepositoryInterface,
    userRepository: UserRepositoryInterface,
    private val teamsRepository: TeamsRepositoryInterface
): ViewModel() {

    private val _teams: MutableStateFlow<List<Team>?> = MutableStateFlow(null)
    val teams = _teams.asStateFlow()

    private val _currentTeam: MutableStateFlow<Team?> = MutableStateFlow(null)
    val currentTeam = _currentTeam.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy = _isBusy.asStateFlow()

    private val _challenges: MutableStateFlow<List<Challenge>?> = MutableStateFlow(null)
    val challenges = _challenges.asStateFlow()

    private val _users: MutableStateFlow<Map<String, List<User>>> = MutableStateFlow(mapOf())
    val users = _users.asStateFlow()

    fun joinTeam(teamId: String) {
        _isBusy.value = true
        viewModelScope.launch {
            teamsRepository.joinTeam(teamId)
            _isBusy.value = false
        }
    }

    fun leaveCurrentTeam() {
        _isBusy.value = true
        viewModelScope.launch {
            teamsRepository.createTeam()

            _isBusy.value = false
        }
    }

    fun createOwnTeam() {
        _isBusy.value = true
        viewModelScope.launch {
            teamsRepository.createTeam()

            _isBusy.value = false
        }
    }

    init {
        viewModelScope.launch {
            run {
                teamsRepository.getTeams().collect { listTeams ->
                    val teamsUsers = listTeams.associate {
                        Pair(it.teamId, it.membersUid.map { member -> userRepository.getUser(member) })
                    }

                    _teams.value = listTeams
                    _users.value = teamsUsers
                }
            }

            _challenges.value = challengeRepository.getChallenges()
        }
    }


    companion object {
        fun getFactory(bountyId: String): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    val application = this[APPLICATION_KEY] as Application
                    val container = AppContainer.getInstance(application)

                    ViewBountyViewModel(
                        bountyId = bountyId,
                        challengeRepository = container.bounties.getChallengeRepository(bountyId),
                        userRepository = container.user,
                        teamsRepository = container.bounties.getTeamRepository(bountyId)
                    )
                }
            }
        }
    }
}