package com.github.geohunt.app.data.exceptions

class BountyNotFoundException(val bid: String): Exception("Bounty $bid not found.")