package com.github.geohunt.app.ui.components.challengecreation

import android.app.Application
import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.R
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.components.utils.viewmodels.exceptionHandler
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.BitmapUtils.resizeBitmapToFit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

class CreateChallengeViewModel(
    private val imageRepository: ImageRepository,
    private val locationRepository: LocationRepositoryInterface,
    private val challengeRepository: ChallengeRepositoryInterface,
    val displaySetting: Boolean,
) : ViewModel() {

    private val _selectedDifficulty: MutableStateFlow<Challenge.Difficulty> =
        MutableStateFlow(Challenge.Difficulty.MEDIUM)
    val selectedDifficulty: StateFlow<Challenge.Difficulty> = _selectedDifficulty

    private val _expirationDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    val expirationDate: StateFlow<LocalDate?> = _expirationDate

    fun withDifficulty(difficulty: Challenge.Difficulty) {
        require(displaySetting)

        _selectedDifficulty.value = difficulty
    }

    fun withExpirationDate(expirationDate: LocalDate?) {
        require(displaySetting)

        _expirationDate.value = expirationDate
    }

    fun create(location: Location,
               localPicture: LocalPicture,
               onFailure: (Throwable) -> Unit = {},
               onSuccess: (Challenge) -> Unit = {}) {
        viewModelScope.launch(exceptionHandler(onFailure)) {
            val challenge = challengeRepository.createChallenge(
                localPicture,
                location,
                selectedDifficulty.value,
                DateFormatUtils.atEndOfDay(expirationDate.value)
            )
            onSuccess(challenge)
        }
    }

    fun reset() {
        _expirationDate.value = null
        _selectedDifficulty.value = Challenge.Difficulty.MEDIUM
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                CreateChallengeViewModel(
                    container.image,
                    container.location,
                    container.challenges,
                    true
                )
            }
        }
    }

    class BountyFactory(private val bid : String) : ViewModelProvider.NewInstanceFactory() {

        // This masterpiece is sponsored by developer.android.com
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            // Get the Application object from extras
            val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
            val container = AppContainer.getInstance(application)

            return CreateChallengeViewModel(
                container.image,
                container.location,
                container.bounty.getChallengeRepository(bid),
                false
            ) as T
        }
    }
}

