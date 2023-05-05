package com.github.geohunt.app.model

data class Team(
    val teamId: String,
    val membersUid: List<String>,
    val leaderUid: String
)
