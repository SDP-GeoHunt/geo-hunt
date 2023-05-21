package com.github.geohunt.app.ui.screens.teamprogress

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.data.repository.LocationRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.AuthViewModel
import com.github.geohunt.app.ui.utils.pagination.DynamicPagedList
import com.github.geohunt.app.ui.utils.pagination.StaticPagedList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * [TeamProgressScreen] ViewModel.
 */
class TeamProgressViewModel(
    override val authRepository: AuthRepositoryInterface,
    val userRepository: UserRepositoryInterface,
    val locationRepository: LocationRepositoryInterface,
    val bountiesRepository: BountiesRepositoryInterface,
    val bountyId: String
): AuthViewModel(authRepository) {
    private val teamRepository = bountiesRepository.getTeamRepository(bountyId)
    private val challengeRepository = bountiesRepository.getChallengeRepository(bountyId)
    private val claimRepository = bountiesRepository.getClaimRepository(bountyId)

    enum class TeamStatus {
        // When the user is loading his team members
        LOADING_TEAM,

        // When the user finished loading his team
        // This does *not* mean that all async operations finished, but only that we can proceed
        // with loading members and challenges.
        LOADED_TEAM,

        // If the user has no team.
        ERROR_NO_TEAM
    }

    private val _teamStatus = MutableStateFlow(TeamStatus.LOADING_TEAM)
    val teamStatus: StateFlow<TeamStatus> = _teamStatus.asStateFlow()

    private val _teamName: MutableStateFlow<String?> = MutableStateFlow(null)
    val teamName: StateFlow<String?> = _teamName.asStateFlow()

    private lateinit var _teamMembers: StaticPagedList<User>
    val teamMembers: StaticPagedList<User>
        get() = _teamMembers

    private val _challenges: MutableStateFlow<List<Challenge>?> = MutableStateFlow(null)
    val challenges: StateFlow<List<Challenge>?> = _challenges.asStateFlow()

    private val _newMessages: MutableStateFlow<Int> = MutableStateFlow(0)
    val newMessages: StateFlow<Int> = _newMessages.asStateFlow()

    private val _currentLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _claimState: MutableStateFlow<DynamicPagedList<Boolean>?> = MutableStateFlow(null)
    val claimState: StateFlow<DynamicPagedList<Boolean>?> = _claimState.asStateFlow()

    private suspend fun fetchTeamStatus() {
        _teamStatus.value = TeamStatus.LOADING_TEAM
        teamRepository.getUserTeam().collect { team ->
            when (team) {
                null -> _teamStatus.value = TeamStatus.ERROR_NO_TEAM
                else -> {
                    _teamName.value = team.name
                    _teamMembers = StaticPagedList(
                        list = team.membersUid,
                        fetcher = { userRepository.getUser(it) },
                        coroutineScope = viewModelScope,
                        prefetchSize = 10
                    )

                    _teamStatus.value = TeamStatus.LOADED_TEAM

                    fetchChallenges(team)
                }
            }
        }
    }

    private suspend fun fetchChallenges(team: Team) {
        val challenges = challengeRepository.getChallenges()

        _claimState.value = DynamicPagedList(
            list = challenges,
            fetcher = { challenge -> claimRepository.getRealtimeClaimsOf(team).map {
                claims -> claims.any { it.parentChallengeId == challenge.id }
            } },
            coroutineScope = viewModelScope
        )
        _challenges.value = challenges
    }

    private fun fetchLocation() {
        viewModelScope.launch {
            locationRepository.getLocations(viewModelScope).collect {
                _currentLocation.value = it
            }
        }
    }

    init {
        viewModelScope.launch {
            fetchTeamStatus()
        }

        // Location is fetched in a separate coroutine since it collect indefinitely
        fetchLocation()
    }

    companion object {
        fun getFactory(bountyId: String) = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                TeamProgressViewModel(
                    authRepository = container.auth,
                    userRepository = container.user,
                    locationRepository = container.location,
                    bountiesRepository = container.bounty,
                    bountyId = bountyId
                )
            }
        }
    }
}