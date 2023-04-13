package com.github.geohunt.app.model.database.api

import com.github.geohunt.app.model.LazyRef
import com.google.android.gms.tasks.Task

/**
 * This interface defines a context for logged-in users, containing properties and methods
 * related to follow/unfollow other users.
 */
interface LoggedUserContext {
    /**
     * This property represents the current logged user
     */
    val loggedUser : LazyRef<User>

    /**
     * This property represents whether the current user follows this user.
     */
    val User.doesFollow : Task<Boolean>

    /**
     * This property represents whether the user is the logged-in user.
     */
    val User.isLoggedUser : Boolean
        get() = this.uid == loggedUser.id

    /**
     * This property represents whether the given lazy-ref of user is a lazy-ref
     * to the current logged-in user
     */
    val LazyRef<User>.isLoggedUser : Boolean
        get() = this.id == loggedUser.id

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
 * @param context The LoggedUserContext to use.
 * @param callback The code block to execute.
 * @return The result of executing the code block.
 */
fun <R> withContext(context: LoggedUserContext, callback: LoggedUserContext.() -> R) : R {
    return context.callback()
}
