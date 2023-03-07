package com.github.geohunt.app.model.database.firebase

import com.github.geohunt.app.model.database.api.PictureImage
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.database.models.ChallengeVisibility
import java.time.LocalDateTime

data class FirebaseChallenge(
    override val cid: String,
    override val uid: String,
    override var thumbnail: PictureImage,
    override val published: LocalDateTime,
    override val visibility: ChallengeVisibility) : Challenge
{

}
