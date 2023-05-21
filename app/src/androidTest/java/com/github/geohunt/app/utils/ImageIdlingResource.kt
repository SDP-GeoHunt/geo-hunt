package com.github.geohunt.app.utils

import androidx.compose.ui.test.IdlingResource
import coil.EventListener
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import java.util.Collections

/**
 * Creates an [IdlingResource] for [coil.compose.AsyncImage] that informs UI tests that the app is busy
 * until all images (from network) finish loading.
 *
 * Adapted from [Coil's Github samples](https://github.com/coil-kt/coil/blob/main/coil-compose-base/src/androidTest/java/coil/compose/utils/ImageLoaderIdlingResource.kt).
 */
class ImageIdlingResource : EventListener, IdlingResource {
    private val ongoingRequests = Collections.synchronizedSet(mutableSetOf<ImageRequest>())
    private val _results = Collections.synchronizedList(mutableListOf<ImageResult>())

    @field:Volatile var startedRequests = 0
        private set

    @field:Volatile var finishedRequests = 0
        private set

    override val isIdleNow get() = ongoingRequests.isEmpty()

    override fun onStart(request: ImageRequest) {
        ongoingRequests += request
        startedRequests++
    }

    override fun onCancel(request: ImageRequest) {
        ongoingRequests -= request
    }

    override fun onError(request: ImageRequest, result: ErrorResult) {
        ongoingRequests -= request
        _results += result
        finishedRequests++
    }

    override fun onSuccess(request: ImageRequest, result: SuccessResult) {
        ongoingRequests -= request
        _results += result
        finishedRequests++
    }
}
