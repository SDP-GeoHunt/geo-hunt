package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.User

fun mockUser(
    id: String = "dn1",
    displayName: String = "Here's Johny",
    profilePictureUrl : String = "http://10.0.2.2:9199/geohunt-1.appspot.com/images/challenges-images.png"
) : User {
    return User(id,
        displayName,
        profilePictureUrl)
}
