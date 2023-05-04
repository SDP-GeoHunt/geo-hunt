package com.github.geohunt.app.data.exceptions

/**
 * Special exception thrown whenever a given user is not found within the database
 */
class UserNotFoundException(val id: String) : Exception("User $id not found.")