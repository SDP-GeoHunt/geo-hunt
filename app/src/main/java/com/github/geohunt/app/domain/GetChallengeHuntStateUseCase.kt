package com.github.geohunt.app.domain

import com.github.geohunt.app.data.repository.ActiveHuntsRepositoryInterface
import com.github.geohunt.app.data.repository.ClaimRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.components.buttons.ChallengeHuntState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetChallengeHuntStateUseCase(
    private val claimRepository: ClaimRepositoryInterface,
    private val activeHuntsRepository: ActiveHuntsRepositoryInterface
) {
    fun getChallengeHuntState(challenge: Challenge): Flow<ChallengeHuntState> {
        return flow {
            if (claimRepository.doesClaim(challenge)) {
                emit(ChallengeHuntState.CLAIMED)
            } else {
                emitAll(activeHuntsRepository.isHunting(challenge).map {
                    if (it) ChallengeHuntState.HUNTED else ChallengeHuntState.NOT_HUNTED
                })
            }
        }
    }
}