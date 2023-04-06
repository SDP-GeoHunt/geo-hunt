package com.github.geohunt.app.model.database.api

/**
 * Special exception thrown whenever a given user is not found within the database
 */
class UserNotFoundException(val id: String) : Exception("User $id not found.")