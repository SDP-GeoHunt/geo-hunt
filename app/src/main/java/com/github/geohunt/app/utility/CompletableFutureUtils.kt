package com.github.geohunt.app.utility

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

fun <T> CompletableFuture<T>.exceptionallyCompose(fn: (Throwable) -> CompletableFuture<T>) : CompletableFuture<T> {
    val future = CompletableFuture<CompletableFuture<T>>()

    this.thenAccept {
            future.complete(CompletableFuture.completedFuture(it))
        }
        .exceptionally {
            future.complete(fn(it))
            null
        }

    return future.flatten()
}

fun <T> CompletableFuture<CompletableFuture<T>>.flatten() : CompletableFuture<T> {
    val outputFuture = CompletableFuture<T>()

    this.thenAccept { future ->
        future.thenAccept {
                outputFuture.complete(it)
            }
            .exceptionally {
                outputFuture.completeExceptionally(it)
                null
            }
    }
        .exceptionally {
            outputFuture.completeExceptionally(it)
            null
        }

    return outputFuture
}

fun <T> CompletableFuture<T>.onException(f: (Throwable) -> Unit) {
    this.thenAccept {  }
        .exceptionally {
            f
            null
        }
}
