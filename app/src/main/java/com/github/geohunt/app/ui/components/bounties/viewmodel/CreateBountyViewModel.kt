package com.github.geohunt.app.ui.components.bounties.viewmodel

import android.app.Application
import android.view.View
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.R
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.components.challengecreation.CreateChallengeViewModel
import com.github.geohunt.app.ui.components.utils.PhotoPipelineBaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.exp

class CreateBountyViewModel(
    private val bountyRepository: BountiesRepositoryInterface
) : ViewModel() {

    private val _expirationDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    val expirationDate: StateFlow<LocalDate?> = _expirationDate

    private val _startingDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    val startingDate: StateFlow<LocalDate?> = _startingDate

    private val _location: MutableStateFlow<Location?> = MutableStateFlow(null)
    val location : StateFlow<Location?> = _location

    private val _name : MutableStateFlow<String> = MutableStateFlow("")
    val name : StateFlow<String> = _name

    fun withName(name: String) {
        if (name.length > R.integer.bounty_name_maximum_character) return
        _name.value = name
    }

    fun withLocation(location: Location) {
        _location.value = location
    }

    fun withExpirationDate(expirationDate: LocalDate) {
        if (expirationDate.isBefore(startingDate.value ?: LocalDate.MIN)) {
            return
        }
        _expirationDate.value = expirationDate
    }

    fun withStartingDate(startingDate: LocalDate) {
        if (startingDate.isAfter(expirationDate.value ?: LocalDate.MIN)) {
            return
        }
        _startingDate.value = startingDate
    }

    fun create(onFailure: (Throwable) -> Unit,
               onSuccess: (Bounty) -> Unit) {
        viewModelScope.launch(exceptionHandler(onFailure)) {
            val bounty = bountyRepository.createBounty(
                name = name.value,
                startingDate = startingDate.value!!.atStartOfDay(),
                expirationDate = expirationDate.value!!.atStartOfDay(),
                location = _location.value!!
            )
            onSuccess(bounty)
        }
    }

    private fun exceptionHandler(callback: (Throwable) -> Unit) =
        CoroutineExceptionHandler { _, throwable ->
            callback(throwable)
        }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                CreateBountyViewModel(
                    container.bounty
                )
            }
        }
    }
}