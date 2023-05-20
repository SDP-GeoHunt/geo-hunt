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
import com.github.geohunt.app.data.repository.bounties.BountyClaimRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.components.utils.viewmodels.exceptionHandler
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
    private val locationRepository: LocationRepositoryInterface,
    private val challengeRepository: ChallengeRepositoryInterface,
    private val bountyClaimRepository: BountyClaimRepositoryInterface,
) : ViewModel() {

    private val _challenge = MutableStateFlow<Challenge?>(null)
    val challenge: StateFlow<Challenge?> = _challenge

    fun start(cid: String, onFailure: (Throwable) -> Unit = {}) {
        reset()
        viewModelScope.launch(exceptionHandler(onFailure)) {
            _challenge.value = challengeRepository.getChallenge(cid)
        }
    }

    fun claim(
        location: Location,
        localPicture: LocalPicture,
        onFailure: (Throwable) -> Unit = {},
        onSuccess: (Claim) -> Unit = {}
    ) {
        require(challenge.value != null)
        viewModelScope.launch(exceptionHandler(onFailure)) {
            val claim = bountyClaimRepository.claimChallenge(
                localPicture,
                challenge.value!!,
                location,
            )
            onSuccess(claim)
        }
    }

    override fun onCleared() {
        super.onCleared()
        reset()
    }

    fun reset() {
        _challenge.value = null
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
                        challengeRepository = container.bounty.getChallengeRepository(bountyId),
                        bountyClaimRepository = container.bounty.getClaimRepository(bountyId)
                    )
                }
            }
        }
    }
}
