package com.github.geohunt.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.database.api.Database
import com.github.geohunt.app.model.database.api.LoggedUserContext

/**
 * Helper to use the [Database.logged] function with composable callback
 *
 * @param callback the composable callback to be used within the logged context
 */
@Composable
fun Database.WithLoggedUserContext(callback : @Composable LoggedUserContext.() -> Unit) {
    val loggedUserContext = remember(Authenticator.authInstance.get().user) {
        getLoggedContext()
    }
    loggedUserContext.callback()
}
