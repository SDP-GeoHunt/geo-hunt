package com.github.geohunt.app.ui.screens.teamleaderboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardEntry
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardInformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * The leaderboard for teams in a specific bounty
 * @param teamsRepository the teams repository corresponding to the current bounty,
 *  all the information needed to create entries will be fetched here
 */
class TeamLeaderboardViewModel(
        teamsRepository: TeamsRepositoryInterface
): ViewModel() {
    private val _leaderboardInformation = MutableStateFlow(LeaderboardInformation(listOf(), -1))
    val leaderboardInformation = _leaderboardInformation.asStateFlow()

    //Note that we allow ourselves to fetch every team of a bounty since bounties are on a
    //way smaller scale than Users/UserLeaderboard for example
    init {
        viewModelScope.launch {
            val userTeamId = teamsRepository.getUserTeam().first()!!.teamId
            val teams = teamsRepository.getTeams().first()
            val sortedTeams = teams.sortedBy { it.score }.reversed()
            val userIndex = sortedTeams.indexOfFirst { it.teamId == userTeamId }
            _leaderboardInformation.value = LeaderboardInformation(sortedTeams.map { toEntry(it) }, userIndex)
        }
    }

    private fun toEntry(team: Team): LeaderboardEntry {
        return LeaderboardEntry(
                displayName = team.name,
                score = team.score,
                displayIcon = null
        )
    }

    companion object {
        fun factory(bid: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                TeamLeaderboardViewModel(
                        container.bounty.getTeamRepository(bid)
                )
            }
        }
    }

}