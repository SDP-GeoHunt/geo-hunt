package com.github.geohunt.app.data.exceptions

class ClaimNotFoundException(val id: String): Exception("Claim $id not found.")