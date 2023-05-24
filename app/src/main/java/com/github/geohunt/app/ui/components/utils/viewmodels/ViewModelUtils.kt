package com.github.geohunt.app.ui.components.utils.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler

fun ViewModel.exceptionHandler(callback: (Throwable) -> Unit) =
    CoroutineExceptionHandler { _, throwable ->
        callback(throwable)
    }
