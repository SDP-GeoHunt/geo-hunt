package com.github.geohunt.app.utility

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.cancellation.CancellationException

/**
 * Convert a given [Task] to a [CompletableFuture] that is executed in the context of a certain
 * activity
 *
 * @param activity the activity in which the return CompletableFuture will be executed
 */
fun <TResult> Task<TResult>.toCompletableFuture(activity: Activity) : CompletableFuture<TResult>
{
    val completableFuture = CompletableFuture<TResult>()
    this.addOnSuccessListener(activity) {
            completableFuture.complete(it)
        }
        .addOnFailureListener(activity) {
            completableFuture.completeExceptionally(it)
        }
        .addOnCanceledListener(activity) {
            completableFuture.completeExceptionally(CancellationException())
        }
    return completableFuture
}

/**
 * Converts the given [DataSnapshot] from Firebase to a map.
 *
 * @return The map, where the keys present in the map are mapped to their corresponding Firebase values.
 */
inline fun <reified T> DataSnapshot.toMap(): Map<String, T>
{
    val map = mutableMapOf<String, T>()
    for (child in this.children) {
        if (child.value is T) {
            map[child.key!!] = child.value as T
        }
    }
    return map
}

/**
 * Chains a continuation task to this task, which will be executed if this task completes successfully.
 * The continuation task is defined by the provided function `fn`, which takes the result of this task as its
 * input and returns a new task. The resulting task represents the execution of the continuation task.
 *
 * If this task completes with an exception, the resulting task will also complete with the same exception.
 *
 * @param fn A function that takes the result of this task and returns a new task.
 * @return A task representing the execution of the continuation task.
 */
fun <TResult, U> Task<TResult>.thenDo(fn: (TResult) -> Task<U>) : Task<U>
{
    return this.continueWithTask {
        if (it.isSuccessful) {
            fn(it.result)
        }
        else {
            Tasks.forException(it.exception!!)
        }
    }
}

/**
 * Returns a new task that applies the given function to the result of this task. If this task fails,
 * the returned task will also fail with the same exception.
 *
 * @param fn The function to apply to the result of this task.
 * @param <TResult> The type of the result of this task.
 * @param <U> The type of the result of the new task.
 * @return A new task that applies the given function to the result of this task.
 */
fun <TResult, U> Task<TResult>.thenMap(fn: (TResult) -> U) : Task<U>
{
    return this.continueWithTask {
        if (it.isSuccessful) {
            try {
                Tasks.forResult(fn(it.result))
            }
            catch (e: Exception) {
                Tasks.forException(e)
            }
        }
        else {
            Tasks.forException(it.exception!!)
        }
    }
}
