package com.github.geohunt.app.model.database.api

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.google.android.gms.tasks.Task
import java.time.LocalDateTime

/**
 * This interface defines a context for logged-in users, containing properties and methods
 * related to follow/unfollow other users.
 */
interface LoggedUserContext {
    /**
     * This property represents a reference to the current user
     */
    val loggedUserRef : LiveLazyRef<User>

    /**
     * This property represent whether the current user follows this user.
     */
    val User.doesFollow : LazyRef<Boolean>

    /**
     * This property represents whether the user is the logged-in user.
     */
    val User.isLoggedUser : Boolean
        get() = this.uid == loggedUserRef.id

    /**
     * This property represents whether the given lazy-ref of user is a lazy-ref
     * to the current logged-in user
     */
    val LazyRef<User>.isLoggedUser : Boolean
        get() = this.id == loggedUserRef.id

    /**
     * This method allows the user to follow another user.
     * @return A task that will complete when the user has been successfully followed.
     */
    fun User.follow() : Task<Nothing?>
    fun LazyRef<User>.follow() : Task<Nothing?>

    /**
     * This method allows the user to unfollow another user.
     * @return A task that will complete when the user has been successfully unfollowed.
     */
    fun User.unfollow() : Task<Nothing?>
    fun LazyRef<User>.unfollow() : Task<Nothing?>

    /**
     * The currently logged user insert a like for a specific challenge
     */
    fun Challenge.like() : Task<Nothing?>

    /**
     * The currently logged user remove a previous like for a specific challenge
     */
    fun Challenge.unlike() : Task<Nothing?>

    /**
     * Does the current user likes the provided challenge
     */
    val Challenge.doesLoggedUserLikes : LazyRef<Boolean>

    /**
     * Join a hunt for a specific challenge
     * @return Joining a hunt for a specific challenge
     */
    fun Challenge.joinHunt() : Task<Nothing?>
    fun Challenge.leaveHunt() : Task<Nothing?>

    /**
     * Enables the currently logged users to create a new challenge with the given parameters
     *
     * @param thumbnail the image that will be displayed with the challenge
     * @param location the location where the user took the picture
     * @param expirationDate the date at which the challenge will expire otherwise null
     */
    fun createChallenge(thumbnail: Bitmap,
                        location: Location,
                        difficulty: Challenge.Difficulty,
                        expirationDate: LocalDateTime ?= null,
                        description: String ?= null) : Task<Challenge>


    /**
     * Changes the visibility of the user's profile.
     *
     * @param profileVisibility the profile visibility
     */
    fun setProfileVisibility(profileVisibility: ProfileVisibility): Task<Void>

    /**
     * Update the currently logged user with according data
     *
     * @param editedUser delta-increment of the data to be changed within the user
     */
    fun updateLoggedUser(editedUser: EditedUser) : Task<Nothing?>

    /**
     * Enables the currently logged user to submit a claim to a specific challenge
     *
     * @param thumbnail the image that will be displayed with the claim
     * @param location the location ta which the claim will be submitted
     */
    fun Challenge.submitClaim(thumbnail: Bitmap, location: Location) : Task<Claim>

    /**
     * This method retrieves a list of users that the current logged user is following.
     *
     * @return A task that will complete with the list of users that the current user is following.
     */
    fun getFollowedUsers() : Task<List<LazyRef<User>>>

    /**
     * This method retrieves a list of users that the current logged user is followed by
     *
     * @return A task that will complete with the list of users that follows the current user
     */
    fun getFollowers() : Task<List<LazyRef<User>>>
}

/**
 * This function allows a code block to be executed with a given LoggedUserContext.
 *
 * @param callback The code block to execute.
 * @return The result of executing the code block.
 */
fun <R> LoggedUserContext.executeWithContext(callback: LoggedUserContext.() -> R) : R {
    return callback(this)
}
