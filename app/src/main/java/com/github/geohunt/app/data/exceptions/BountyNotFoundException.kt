package com.github.geohunt.app.data.exceptions

class BountyNotFoundException(private val bid: String): Exception("Bounty $bid not found.")