package com.github.geohunt.app.data.network.firebase.models

import com.github.geohunt.app.model.Location

data class FirebaseBountyMetadata(
    val adminUid: String? = null,
    val name: String? = null,
    val startingDate: String? = null,
    val expirationDate: String? = null,
    val location: Location? = null
)
