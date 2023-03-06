package com.github.geohunt.app.utility

import android.app.Activity
import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.cancellation.CancellationException

fun <TResult> attackTaskCompletionSourceToTask(taskCompletionSource: TaskCompletionSource<TResult>,
                                               task: Task<TResult>) {
    task
            .addOnSuccessListener { result ->
                taskCompletionSource.setResult(result)
            }
            .addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }
            .addOnCanceledListener {
                taskCompletionSource.setException(CancellationException())
            }
}

fun <TResult, U> Task<TResult>.map(fn: (TResult) -> U) : Task<U> {
    return this.onSuccessTask {
        Tasks.forResult(fn(it))
    }
}

fun <TResult, U> Task<TResult>.flatMap(fn: (TResult) -> Task<U>) : Task<U> {
    return this.onSuccessTask {
        fn(it)
    }
}

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
