package com.github.geohunt.app.mocks

import com.github.geohunt.app.ui.controller.NavController

class MockNavController : NavController {
    private var route : String = ""

    fun getAndResetRoute() : String {
        val oldRoute = route
        route = ""
        return oldRoute
    }

    fun reset() {
        route = ""
    }

    override fun navigateTo(route: String) {
        this.route = route
    }

    override fun goBack() {
        this.route = ".."
    }
}