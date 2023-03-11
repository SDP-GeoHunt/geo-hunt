package com.github.geohunt.app

import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.authentication.FirebaseAuthenticator

class ServiceLocator {
    companion object {
        private var authenticator: Authenticator = FirebaseAuthenticator()

        fun getAuthenticator(): Authenticator {
            return authenticator
        }

        fun setAuthenticator(authenticator: Authenticator) {
            ServiceLocator.authenticator = authenticator
        }
    }
}