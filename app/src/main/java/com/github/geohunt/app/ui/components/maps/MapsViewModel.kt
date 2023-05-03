package com.github.geohunt.app.ui.components.maps

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

    fun retrieveChallengesSingleHash(sectorHash: String): StateFlow<List<Challenge?>> {
        val mutableStateFlow = MutableStateFlow<List<Challenge?>>(listOf())

        viewModelScope.launch {

                val challengeFlow = challengeRepository.getSectorChallenges(sectorHash)

                challengeFlow.collect(){
                    mutableStateFlow.value = it
                    Log.d("MapsViewModel", "Challenge:q $it")
                }

        }

        return mutableStateFlow.asStateFlow()
    }
}
