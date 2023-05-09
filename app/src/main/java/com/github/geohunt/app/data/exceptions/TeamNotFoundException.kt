package com.github.geohunt.app.data.exceptions

class TeamNotFoundException(val id: String): Exception("Team $id not found.")
