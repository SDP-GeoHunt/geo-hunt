package com.github.geohunt.app.ui.components.bounties.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Team
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminBountyViewModel(
    private val bountiesRepository: BountiesRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val bid_ = MutableStateFlow<String?>(null)
    val bid : StateFlow<String?> = bid_

    private val bounty_ = MutableStateFlow<Bounty?>(null)
    val bounty : StateFlow<Bounty?> = bounty_

    private val challenges_ = MutableStateFlow<List<Challenge>>(listOf())
    val challenges : StateFlow<List<Challenge>> = challenges_

    var teamsFlow : Flow<List<Team>>? = null

    private var teamRepository: TeamsRepositoryInterface? = null
    private var challengeRepository: ChallengeRepositoryInterface? = null

    fun setBountyName(name: String) {
        require(bounty_.value != null)

        viewModelScope.launch {
            bountiesRepository.renameBounty(bounty_.value!!, name)
            refresh()
        }
    }

    fun withBusinessId(bid: String, onFailure: (Throwable) -> Unit) {
        reset()
        bid_.value = bid


        viewModelScope.launch(onFailure) {
            teamRepository = bountiesRepository.getTeamRepository(bid)
            challengeRepository = bountiesRepository.getChallengeRepository(bid)

            teamsFlow = teamRepository!!.getTeams()

            val bounty = bountiesRepository.getBountyById(bid)
            require(bounty.adminUid == userRepository.getCurrentUser().id)

            refresh(onFailure)

            bounty_.value = bounty
        }
    }

    fun refresh(onFailure: (Throwable) -> Unit = {}) {
        require(challengeRepository != null)

        viewModelScope.launch(onFailure) {
            challenges_.value = challengeRepository!!.getChallenges()
            bounty_.value = bountiesRepository.getBountyById(bounty_.value!!.bid)
        }
    }

    private fun reset() {
        bid_.value = null
        bounty_.value = null
        teamRepository = null
        challengeRepository = null
        teamsFlow = null
    }

    private fun CoroutineScope.launch(onFailure: (Throwable) -> Unit, callback: suspend CoroutineScope.() -> Unit) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            onFailure(throwable)
        }
        this.launch(context = exceptionHandler) {
            callback()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                AdminBountyViewModel(
                    container.bounty,
                    container.user
                )
            }
        }
    }
}