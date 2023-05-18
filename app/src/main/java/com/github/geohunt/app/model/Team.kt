package com.github.geohunt.app.model

/**
 * Represents a team in the application
 *
 * @property teamId the unique identifier of the team
 * @property membersUid the list of unique identifiers of each members of the team
 * @property leaderUid the unique identifier of the leader of the team
 */
data class Team(
    val teamId: String, // the teamId used to index this specific team in database
    val name: String, // Name of the team
    val membersUid: List<String>, // a list of all members of this team
    val leaderUid: String, // the user-id of the leader of this team
    val score: Long, // number of points the current team was awarded
)
