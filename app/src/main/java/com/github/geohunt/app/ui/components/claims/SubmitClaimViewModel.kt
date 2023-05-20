package com.github.geohunt.app.ui.components.claims

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.R
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.*
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

class SubmitClaimViewModel(
    private val challengeRepository: ChallengeRepository,
    private val claimRepository: ClaimRepository
) : ViewModel() {

    private val _challenge = MutableStateFlow<Challenge?>(null)
    val challenge: StateFlow<Challenge?> = _challenge

    fun start(cid: String, onFailure: (Throwable) -> Unit = {}) {
        reset()
        viewModelScope.launch(exceptionHandler(onFailure)) {
            _challenge.value = challengeRepository.getChallenge(cid)
        }
    }

    fun claim(location: Location,
              localPicture: LocalPicture,
              onFailure: (Throwable) -> Unit = {},
              onSuccess: (Claim) -> Unit = {}) {
        require(challenge.value != null)

        viewModelScope.launch(exceptionHandler(onFailure)) {
            val claim = claimRepository.claimChallenge(
                localPicture,
                location,
                challenge.value!!
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
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                SubmitClaimViewModel(
                    container.challenges,
                    container.claims
                )
            }
        }
    }
}