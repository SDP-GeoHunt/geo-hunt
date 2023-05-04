package com.github.geohunt.app.ui.screens.maps

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.geohunt.app.data.repository.AuthRepository
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.UserRepository
import com.github.geohunt.app.model.Challenge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapsViewModel() : ViewModel() {
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


        viewModelScope.launch {
            for (sectorHash in sectorHashes) {
                val challengeFlow = challengeRepository.getSectorChallenges(sectorHash)

                challengeFlow.collect(){
                    _challenges.value = it
                    Log.d("MapsViewModel", "Challenge:q $it")
                }
            }
        }

        //return mutableStateFlow.asStateFlow()
    }
}
