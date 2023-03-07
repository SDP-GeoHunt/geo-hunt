package com.github.geohunt.app.model.database.api

import com.github.geohunt.app.model.database.api.PictureImage

interface User {
    var displayName: String?

    val uid: String

    val profilePicture: PictureImage?

    val challenges: List<String>
    val hunts: List<String>

    var score: Number
}