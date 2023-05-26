package com.github.geohunt.app.ui.screens.bounty_team_select

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
import com.github.geohunt.app.data.repository.bounties.ActiveBountiesRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
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
class BountyTeamSelectViewModel(
    private val bountyId: String,
    private val bountiesRepository: BountiesRepositoryInterface,
    private val challengeRepository: ChallengeRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val activeBountiesRepository: ActiveBountiesRepositoryInterface,
    private val teamsRepository: TeamsRepositoryInterface
): ViewModel() {

    private val _bountyName: MutableStateFlow<String?> = MutableStateFlow(null)
    val bountyName = _bountyName.asStateFlow()

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

    private val _canDeleteTeams: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    val canDeleteTeams = _canDeleteTeams.asStateFlow()

    fun joinTeam(teamId: String) {
        _isBusy.value = true
        viewModelScope.launch {
            teamsRepository.joinTeam(teamId)
            activeBountiesRepository.joinBounty(bountyId)

            _isBusy.value = false
        }
    }

    fun leaveCurrentTeam() {
        _isBusy.value = true
        viewModelScope.launch {
            teamsRepository.leaveTeam()
            activeBountiesRepository.leaveBounty(bountyId)

            _isBusy.value = false
        }
    }

    fun checkNotAdmin(callbackIfAdmin: () -> Unit) {
        viewModelScope.launch {
            val selfId = userRepository.getCurrentUser().id
            val adminId = bountiesRepository.getBountyById(bountyId)
                .adminUid
            if (selfId == adminId) {
                callbackIfAdmin()
            }
        }
    }

    fun createOwnTeam(name: String) {
        _isBusy.value = true
        viewModelScope.launch {
            teamsRepository.createTeam(name)
            activeBountiesRepository.joinBounty(bountyId)

            _isBusy.value = false
        }
    }

    fun deleteTeam(team: Team) {
        _isBusy.value = true
        viewModelScope.launch  {
            teamsRepository.deleteTeam(team)
            activeBountiesRepository.leaveBounty(bountyId)

            _isBusy.value = false
        }
    }

    init {
        viewModelScope.launch {
            _bountyName.value = bountiesRepository.getBountyById(bountyId).name

            launch {
                teamsRepository.getTeams().collect { listTeams ->
                    val teamsUsers = listTeams.associate {
                        Pair(it.teamId, it.membersUid.map { member -> userRepository.getUser(member) })
                    }

                    _canDeleteTeams.value = listTeams.filter { it.leaderUid == userRepository.getCurrentUser().id }.map { it.teamId }
                    _teams.value = listTeams
                    _users.value = teamsUsers
                }
            }

            launch {
                teamsRepository.getUserTeam().collect {
                    _currentTeam.value = it
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

                    BountyTeamSelectViewModel(
                        bountyId = bountyId,
                        bountiesRepository = container.bounty,
                        challengeRepository = container.bounty.getChallengeRepository(bountyId),
                        userRepository = container.user,
                        activeBountiesRepository = container.activeBounties,
                        teamsRepository = container.bounty.getTeamRepository(bountyId)
                    )
                }
            }
        }
    }
}