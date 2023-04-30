package com.github.geohunt.app.data.exceptions

class ChallengeNotFoundException(val id: String): Exception("Challenge $id not found.")