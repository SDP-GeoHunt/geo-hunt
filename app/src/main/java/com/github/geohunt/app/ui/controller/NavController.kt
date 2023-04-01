package com.github.geohunt.app.ui.controller

/**
 * A NavController interface that ease the process of testing the application
 * while keeping the number of parameters to function as a minimum
 */
interface NavController {
    /**
     * Navigate to a specific route
     *
     * @param route The name of the resource to navigate to
     */
    fun navigateTo(route: String)

    /**
     * Going back to the previous page in the navigation
     */
    fun goBack()
}
