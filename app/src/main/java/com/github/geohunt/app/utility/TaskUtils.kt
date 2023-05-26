package com.github.geohunt.app.utility

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

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
