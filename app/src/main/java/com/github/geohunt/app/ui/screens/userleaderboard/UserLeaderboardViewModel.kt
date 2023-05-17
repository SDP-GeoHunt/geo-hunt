package com.github.geohunt.app.ui.screens.userleaderboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.ScoreRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardEntry
import com.github.geohunt.app.ui.components.leaderboard.LeaderboardInformation
import com.github.geohunt.app.ui.components.user.ProfileIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * The view model of the user leaderboard
 * @param scoreRepository a score repository from which all the scores will be taken
 * @param userRepository a user repository, complementary information about the entries will get fetched here
 */
class UserLeaderboardViewModel(
        scoreRepository: ScoreRepositoryInterface,
        userRepository: UserRepositoryInterface
): ViewModel() {

    private val _leaderboardInformation = MutableStateFlow(LeaderboardInformation(listOf(), -1))
    val leaderboardInformation = _leaderboardInformation.asStateFlow()

    init {
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUser().id
            val topUsers = scoreRepository.getTopNUsers(N)
            val entries = topUsers.map {
                toEntry(user = userRepository.getUser(it.first), score = it.second)
            }
            val userIndex = topUsers.indexOfFirst { it.first == currentUser}
            //Write both values at the same moment to avoid useless recomposing
            _leaderboardInformation.value = LeaderboardInformation(entries, userIndex)
        }
    }

    private fun toEntry(user: User, score: Long): LeaderboardEntry {
        return LeaderboardEntry(
                displayName = user.name,
                score = score,
                displayIcon = user.profilePictureUrl?.let{ { ProfileIcon(user = user) } }
        )
    }

    companion object {
        /**
         * The amount of entries we want our leaderboard to display
         */
        const val N = 100
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                UserLeaderboardViewModel(
                        container.score,
                        container.user
                )
            }
        }
    }
}

