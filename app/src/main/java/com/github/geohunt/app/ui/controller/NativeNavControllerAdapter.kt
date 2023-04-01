package com.github.geohunt.app.ui.controller

import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController

/**
 * An adapter for the native android NavController
 *
 * @param navHostController the native navigation controller to be adapted
 */
class NativeNavControllerAdapter(private val navHostController: NavHostController) : NavController {
    override fun navigateTo(route: String) {
        navHostController.navigate(route)
    }

    override fun goBack() {
        navHostController.popBackStack()
    }
}