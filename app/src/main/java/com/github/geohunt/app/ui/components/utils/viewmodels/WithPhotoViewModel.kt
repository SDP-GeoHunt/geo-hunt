package com.github.geohunt.app.ui.components.utils.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.R
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.BitmapUtils.resizeBitmapToFit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class WithPhotoViewModel(
    val imageRepository: ImageRepository
) : ViewModel() {

    private val imageState_ = MutableStateFlow<File?>(null)
    val imageState : StateFlow<File?> = imageState_

    private val bitmapState_ = MutableStateFlow<Bitmap?>(null)
    val bitmapState : StateFlow<Bitmap?> = bitmapState_

    fun withPhotoFile(fileFactory: (String) -> File, file: File, onFailure: (Throwable) -> Unit) {
        require(imageState_.value == null)

        viewModelScope.launch(exceptionHandler(onFailure)) {
            Log.i("GeoHunt", "Loading image from file at ${file.absolutePath}")
            var bitmap = BitmapUtils.loadFromFile(file)

            // Notice that the line below is explicitly done twice, however
            // the second time no work is required (because image already fit).
            // This is done so that for high-resolution device, it does not go other
            // the limit of bitmap painter
            Log.i("GeoHunt", "Rescale down the image")
            bitmap = bitmap.resizeBitmapToFit(R.integer.maximum_number_of_pixel_per_photo)

            bitmapState_.value = bitmap

            Log.i("GeoHunt", "Preprocess the image")
            imageState_.value = imageRepository.preprocessImage(bitmap, fileFactory)
        }
    }

    fun reset() {
        imageState_.value = null
        bitmapState_.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)
                WithPhotoViewModel(container.image)
            }
        }
    }
}