package com.github.geohunt.app.ui.screens.maps

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.screens.activehunts.ActiveHuntsViewModel
import com.github.geohunt.app.utility.aggregateFlows
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow


class MapsViewModel(
    challengeRepository: ChallengeRepository
) : ViewModel() {
    val image = ImageRepository()
    val auth = AuthRepository()
    val user = UserRepository(image, auth)
    val challengeRepository = ChallengeRepository(user, image, auth)

    private val _challenges: MutableStateFlow<List<Challenge>?> = MutableStateFlow(null)
    val challenges: StateFlow<List<Challenge>?> = _challenges.asStateFlow()

    /*fun retrieveChallengesSingleHash(sectorHash: String): StateFlow<List<Challenge?>> {
        viewModelScope.launch {
            val challengeFlow = challengeRepository.getSectorChallenges(sectorHash)

            challengeFlow.collect(){
                mutableStateFlow.value = it
                Log.d("MapsViewModel", "Challenge:q $it")
            }
        }

        return mutableStateFlow.asStateFlow()
    }*/

    fun retrieveChallengesMultiHash(sectorHashes: List<String>) {


        //viewModelScope.launch {
            //challengeRepository.getSectorChallenges().collect()

            val aggregateChallengeFlow = sectorHashes.map { sectorHash ->
                challengeRepository.getSectorChallenges(sectorHash)
            }.aggregateFlows()

        viewModelScope.launch {

                aggregateChallengeFlow.collect {
                _challenges.value = it
                Log.d("MapsViewModel", "Challenge:q $it")
            }
        }

            /*val fetchedChallenges = challenges.aggregateFlows().map {
                it
            }

            for (sectorHash in sectorHashes) {
                val challengeFlow = challengeRepository.getSectorChallenges(sectorHash)

                challengeFlow.collect(){
                    _challenges.value = it
                    Log.d("MapsViewModel", "Challenge:q $it")
                }
            }*/
        //}

        //return mutableStateFlow.asStateFlow()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                MapsViewModel(
                    container.challenges
                )
            }
        }
    }
}
