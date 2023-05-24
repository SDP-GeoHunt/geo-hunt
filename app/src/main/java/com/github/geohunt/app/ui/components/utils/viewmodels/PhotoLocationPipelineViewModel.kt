package com.github.geohunt.app.ui.components.utils.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.LocationRepositoryInterface
import com.github.geohunt.app.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoLocationPipelineViewModel(
    val locationRepository: LocationRepositoryInterface,
) : ViewModel() {

    private val location_ = MutableStateFlow<Location?>(null)
    val location : StateFlow<Location?> = location_

    fun startLocationUpdate(onFailure: (Throwable) -> Unit = {}) {
        viewModelScope.launch(exceptionHandler(onFailure)) {
            locationRepository.getLocations(viewModelScope).collect {
                location_.value = it
            }
        }
    }

    fun reset() {
        location_.value = null
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                PhotoLocationPipelineViewModel(
                    container.location
                )
            }
        }
    }
}
