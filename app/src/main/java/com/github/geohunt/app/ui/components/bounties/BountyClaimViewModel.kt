package com.github.geohunt.app.ui.components.bounties

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.R
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.data.repository.bounties.BountiesRepository
import com.github.geohunt.app.data.repository.bounties.BountyClaimRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.BitmapUtils.resizeBitmapToFit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class BountyClaimViewModel(
    private val bountyId: String,
    private val imageRepository: ImageRepository,
    private val locationRepository: LocationRepository,
    private val challengeRepository: ChallengeRepositoryInterface,
    private val bountiesRepository: BountiesRepository,
    private val bountyClaimRepositoryInterface: BountyClaimRepositoryInterface,
    private val bountyTeamsRepository: TeamsRepositoryInterface,

    ) : ViewModel() {

    enum class State {
        AWAITING_CHALLENGE,
        AWAITING_CAMERA,
        AWAITING_LOCATION_PERMISSION,
        AWAITING_LOCATION,
        READY_TO_CLAIM,
        CLAIMING
    }

    private val _location : MutableStateFlow<Location?> = MutableStateFlow(null)
    val location : StateFlow<Location?> = _location

    private val _submittingState : MutableStateFlow<State> =
        MutableStateFlow(State.AWAITING_CHALLENGE)
    val submittingState : StateFlow<State> = _submittingState

    private val _photoState = MutableStateFlow<Bitmap?>(null)
    val photoState: StateFlow<Bitmap?> = _photoState

    private val _challenge = MutableStateFlow<Challenge?>(null)
    val challenge: StateFlow<Challenge?> = _challenge

    fun start(cid: String, onFailure: (Throwable) -> Unit = {}) {
        reset()
        viewModelScope.launch(exceptionHandler(onFailure)) {
            _challenge.value = challengeRepository.getChallenge(cid)
            _submittingState.value = State.AWAITING_CAMERA
        }
    }

    fun claim(fileFactory: (String) -> File,
              onFailure: (Throwable) -> Unit = {},
              onSuccess: (Claim) -> Unit = {}) {
        require(submittingState.value == State.READY_TO_CLAIM)
        require(location.value != null)
        require(photoState.value != null)
        require(challenge.value != null)

        viewModelScope.launch(exceptionHandler(onFailure)) {
            val file = imageRepository.preprocessImage(photoState.value!!, fileFactory)
            _submittingState.value = State.CLAIMING

            val claim = bountyClaimRepositoryInterface.claimChallenge(
                LocalPicture(file),
                challenge.value!!,
                location.value!!,
            )

            onSuccess(claim)
        }
    }

    fun startLocationUpdate(onFailure: (Throwable) -> Unit = {}) {
        require(_submittingState.value == State.AWAITING_LOCATION_PERMISSION)

        _submittingState.value = State.AWAITING_LOCATION
        viewModelScope.launch(exceptionHandler(onFailure)) {
            locationRepository.getLocations(viewModelScope).collect {
                _location.value = it
                if (_submittingState.value == State.AWAITING_LOCATION) {
                    _submittingState.value = State.READY_TO_CLAIM
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

    override fun onCleared() {
        super.onCleared()
        reset()
    }

    fun reset() {
        _location.value = null
        _submittingState.value = State.AWAITING_CAMERA
        _photoState.value = null
        _challenge.value = null
    }

    private fun exceptionHandler(callback: (Throwable) -> Unit) =
        CoroutineExceptionHandler { _, throwable ->
            callback(throwable)
        }

    companion object {
        fun getFactory(bountyId: String): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    val application = this[APPLICATION_KEY] as Application
                    val container = AppContainer.getInstance(application)

                    BountyClaimViewModel(
                        bountyId = bountyId,
                        imageRepository = container.image,
                        locationRepository = container.location,
                        challengeRepository = container.bounties.getChallengeRepository(bountyId),
                        bountiesRepository = container.bounties,
                        bountyClaimRepositoryInterface = container.bounties.getClaimRepository(bountyId),
                        bountyTeamsRepository = container.bounties.getTeamRepository(bountyId)                    )
                }
            }
        }
    }
}
