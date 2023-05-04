package com.github.geohunt.app.ui.components.challengecreation

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.R
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.ChallengeRepository
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.ui.screens.activehunts.ActiveHuntsViewModel
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.BitmapUtils.resizeBitmapToFit
import com.github.geohunt.app.utility.Singleton
import com.github.geohunt.app.utility.quantize
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

class CreateChallengeViewModel(
    private val imageRepository: ImageRepository,
    private val locationRepository: LocationRepository,
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    enum class State {
        AWAITING_LOCATION_PERMISSION,
        AWAITING_CAMERA,
        AWAITING_LOCATION,
        READY_TO_CREATE,
        CREATING
    }

    private val _location : MutableStateFlow<Location?> = MutableStateFlow(null)
    val location : StateFlow<Location?> = _location

    private val _expirationDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    val expirationDate: StateFlow<LocalDate?> = _expirationDate

    private val _selectedDifficulty: MutableStateFlow<Challenge.Difficulty> =
        MutableStateFlow(Challenge.Difficulty.MEDIUM)
    val selectedDifficulty: StateFlow<Challenge.Difficulty> = _selectedDifficulty

    private val _submittingState : MutableStateFlow<State> =
        MutableStateFlow(State.AWAITING_CAMERA)
    val submittingState : StateFlow<State> = _submittingState


    private val _photoState = MutableStateFlow<Bitmap?>(null)
    val photoState: StateFlow<Bitmap?> = _photoState

    fun withDifficulty(difficulty: Challenge.Difficulty) {
        _selectedDifficulty.value = difficulty
    }

    fun withExpirationDate(expirationDate: LocalDate?) {
        _expirationDate.value = expirationDate
    }

    fun create(fileFactory: (String) -> File,
               onFailure: (Throwable) -> Unit = {},
               onSuccess: (Challenge) -> Unit = {}) {
        require(submittingState.value == State.READY_TO_CREATE)
        require(location.value != null)
        require(photoState.value != null)

        viewModelScope.launch(exceptionHandler(onFailure)) {
            val file = imageRepository.preprocessImage(photoState.value!!, fileFactory)
            _submittingState.value = State.CREATING

            val challenge = challengeRepository.createChallenge(
                LocalPicture(file),
                location.value!!,
                selectedDifficulty.value,
                DateFormatUtils.atEndOfDay(expirationDate.value)
            )

            onSuccess(challenge)
        }
    }

    fun startLocationUpdate(onFailure: (Throwable) -> Unit = {}) {
        require(_submittingState.value == State.AWAITING_LOCATION_PERMISSION)

        _submittingState.value = State.AWAITING_LOCATION
        viewModelScope.launch(exceptionHandler(onFailure)) {
            locationRepository.getLocations(viewModelScope).collect {
                _location.value = it
                if (_submittingState.value == State.AWAITING_LOCATION) {
                    _submittingState.value = State.READY_TO_CREATE
                }
            }
        }
    }

    fun withPhoto(file: File, onFailure: (Throwable) -> Unit = {}) {
        require(_submittingState.value == State.AWAITING_CAMERA)

        withPhoto({ BitmapUtils.loadFromFile(file) }, onFailure)
    }

    fun withPhoto(bitmapFactory: suspend () -> Bitmap, onFailure: (Throwable) -> Unit = {}) {
        require(_submittingState.value == State.AWAITING_CAMERA)

        _submittingState.value = State.AWAITING_LOCATION_PERMISSION
        viewModelScope.launch(exceptionHandler(onFailure)) {
            _photoState.value = bitmapFactory().resizeBitmapToFit(R.integer.maximum_number_of_pixel_per_photo)
        }
    }

    private fun exceptionHandler(callback: (Throwable) -> Unit) =
        CoroutineExceptionHandler { _, throwable ->
            callback(throwable)
        }

    override fun onCleared() {
        super.onCleared()
        reset()
    }

    fun reset() {
        _location.value = null
        _submittingState.value = State.AWAITING_CAMERA
        _expirationDate.value = null
        _photoState.value = null
        _selectedDifficulty.value = Challenge.Difficulty.MEDIUM
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                CreateChallengeViewModel(
                    container.image,
                    container.location,
                    container.challenges
                )
            }
        }
    }
}

