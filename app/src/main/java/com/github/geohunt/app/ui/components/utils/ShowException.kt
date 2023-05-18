package com.github.geohunt.app.ui.components.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException

@Composable
fun ShowException(e: Throwable) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            when(e) {
                is UserNotLoggedInException -> "You are not logged in."
                else -> "Unexpected error has happened."
            }
        )
    }
}

// Preview
private class SampleExceptionProvider: PreviewParameterProvider<Throwable> {
    override val values: Sequence<Throwable>
        get() = sequenceOf(UserNotLoggedInException())
}

@Preview
@Composable
private fun PreviewShowException(@PreviewParameter(SampleExceptionProvider::class) e: Throwable) {
    ShowException(e)
}