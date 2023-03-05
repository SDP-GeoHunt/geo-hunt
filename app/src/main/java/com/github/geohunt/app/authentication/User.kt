package com.github.geohunt.app.authentication

interface User {
    var displayName: String?

    val uid: String
}