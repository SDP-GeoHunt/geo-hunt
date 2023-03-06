package com.github.geohunt.app.database.models

import java.time.LocalDateTime

enum class ChallengeVisibility {
    Public,
    FriendOnly,
    Private
}

interface Challenge
{
    val cid : String
    val uid : String
    val published : LocalDateTime
    val thumbnail : PictureImage
    val visibility : ChallengeVisibility
}

