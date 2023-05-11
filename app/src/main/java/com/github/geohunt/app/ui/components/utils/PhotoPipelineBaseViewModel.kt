package com.github.geohunt.app.ui.components.utils

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.geohunt.app.R
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.BitmapUtils.resizeBitmapToFit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

abstract class PhotoPipelineBaseViewModel<T>(
    private val imageRepository: ImageRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    enum class State {
        AWAITING_LOCATION_PERMISSION,
        AWAITING_LOCATION,
        READY_TO_CREATE,
        CREATING
    }

    private val _location : MutableStateFlow<Location?> = MutableStateFlow(null)
    val location : StateFlow<Location?> = _location

    protected val _submittingState : MutableStateFlow<State> =
        MutableStateFlow(State.AWAITING_LOCATION_PERMISSION)
    val submittingState : StateFlow<State> = _submittingState

    private val _photoState = MutableStateFlow<Bitmap?>(null)
    val photoState: StateFlow<Bitmap?> = _photoState

    abstract fun create(fileFactory: (String) -> File,
                        onFailure: (Throwable) -> Unit = {},
                        onSuccess: (T) -> Unit = {})

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
        withPhoto({ BitmapUtils.loadFromFile(file) }, onFailure)
    }

    fun withPhoto(bitmapFactory: suspend () -> Bitmap, onFailure: (Throwable) -> Unit = {}) {
        _submittingState.value = State.AWAITING_LOCATION_PERMISSION
        viewModelScope.launch(exceptionHandler(onFailure)) {
            _photoState.value = bitmapFactory().resizeBitmapToFit(R.integer.maximum_number_of_pixel_per_photo)
        }
    }

    protected fun exceptionHandler(callback: (Throwable) -> Unit) =
        CoroutineExceptionHandler { _, throwable ->
            callback(throwable)
        }

    override fun onCleared() {
        super.onCleared()
        reset()
    }

    open fun reset() {
        _location.value = null
        _submittingState.value = State.AWAITING_LOCATION_PERMISSION
        _photoState.value = null
    }
}