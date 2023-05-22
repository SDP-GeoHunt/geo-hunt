package com.github.geohunt.app.ui.screens.maps

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.data.repository.LocationRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.utility.aggregateFlows
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapsViewModel(
    private val challengeRepository: ChallengeRepository,
    private val locationRepository: LocationRepositoryInterface
) : ViewModel() {
    private val _challenges: MutableStateFlow<List<Challenge>?> = MutableStateFlow(null)
    val challenges: StateFlow<List<Challenge>?> = _challenges.asStateFlow()

    private val _currentLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    fun updateFetchableChallenges(sectorHashes: List<String>) {
        viewModelScope.launch {
            val aggregateChallengeFlow = sectorHashes.map { sectorHash ->
                challengeRepository.getSectorChallenges(sectorHash)
            }.aggregateFlows()

            aggregateChallengeFlow.collect {
                _challenges.value = it
            }
        }
    }

    fun startLocationUpdate() {
        viewModelScope.launch {
            locationRepository.getLocations(viewModelScope).collect {
                _currentLocation.value = it
            }
        }
    }

    fun reset() {
        _currentLocation.value = null
    }

    init {
        startLocationUpdate()
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                MapsViewModel(
                    container.challenges,
                    container.location
                )
            }
        }
    }
}
