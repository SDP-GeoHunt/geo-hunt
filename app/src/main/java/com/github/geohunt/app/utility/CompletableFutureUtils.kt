package com.github.geohunt.app.utility

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

fun <T> CompletableFuture<T>.onException(f: (Throwable) -> Unit) {
    this.thenAccept {  }
        .exceptionally {
            f
            null
        }
}
