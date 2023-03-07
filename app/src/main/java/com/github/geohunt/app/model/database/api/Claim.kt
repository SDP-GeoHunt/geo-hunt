package com.github.geohunt.app.model.database.api

import java.time.LocalDateTime

interface Claim {
    val id: String
    val cid: String
    val uid: String

    val time: LocalDateTime
    val location: Location
}