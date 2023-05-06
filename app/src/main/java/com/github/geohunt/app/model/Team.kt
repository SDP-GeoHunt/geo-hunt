package com.github.geohunt.app.model

/**
 * Represents a team in the application
 *
 * @property teamId the unique identifier of the team
 * @property membersUid the list of unique identifiers of each members of the team
 * @property leaderUid the unique identifier of the leader of the team
 */
data class Team(
    val teamId: String,
    val membersUid: List<String>,
    val leaderUid: String
)
