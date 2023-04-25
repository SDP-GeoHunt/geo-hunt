package com.github.geohunt.app.data.exceptions.auth

/**
 * Thrown when the authentication fails unexpectedly.
 *
 * @see AuthenticationException
 */
class AuthenticationFailureException : AuthenticationException("Unknown authentication failure.")
