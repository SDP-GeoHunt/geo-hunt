package com.github.geohunt.app.data.repository.bounties

import android.graphics.Bitmap
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.Team

interface BountyClaimRepositoryInterface {
    /**
     * Claim a challenge on the behalf of the team the currently logged player is within.
     *
     * @param photo the photo used to claim
     * @param challenge the challenge we are going to claim
     * @param location the location of the player upon claiming
     * @throws [UserNotLoggedInException] if the user is not currently logged
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun claimChallenge(
        photo: LocalPicture,
        challenge: Challenge,
        location: Location
    ) : Claim

    /**
     * Retrieve a list of all claims done by a specific team. Notice that this function does
     * not check the existence of the team, as such if this team does not exists, then this function
     * will simply return an empty-list
     *
     * @param team the team we want to retrieve the claim from
     */
    suspend fun getClaimsOf(team: Team) : List<Claim>

    suspend fun getClaimById(id: String) : Claim
}