package com.github.geohunt.app.model.database.api

interface User {
    var displayName: String

    val uid: String

    val profilePicture: PictureImage?

    val challenges: List<String>
    val hunts: List<String>

    var score: Number
}