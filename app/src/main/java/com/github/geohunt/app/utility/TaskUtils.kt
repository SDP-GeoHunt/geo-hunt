package com.github.geohunt.app.utility

import android.app.Activity
import com.google.android.gms.tasks.Task
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.cancellation.CancellationException

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

