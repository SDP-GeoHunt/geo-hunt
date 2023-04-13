package com.github.geohunt.app

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.cancellation.CancellationException

fun <T> CompletableFuture<T>.linkCompletionTo(other: CompletableDeferred<T>) {
    this.whenComplete { t, u ->
        if (t != null) {
            other.complete(t)
        } else {
            other.completeExceptionally(u)
        }
    }
}

fun <T> Task<T>.linkCompletionTo(other: CompletableDeferred<T>) {
    this.addOnSuccessListener {
            other.complete(it)
        }
        .addOnFailureListener {
            other.completeExceptionally(it)
        }
        .addOnCanceledListener {
            other.completeExceptionally(CancellationException())
        }
}
