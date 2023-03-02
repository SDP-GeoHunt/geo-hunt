package com.github.geohunt.app.utility;

import com.google.android.gms.tasks.Task;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class Conversions {
    private Conversions() {
    }

    public static <T> CompletableFuture<T> taskToCompletableFuture(Task<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();

        task.addOnSuccessListener(future::complete)
                .addOnFailureListener(future::completeExceptionally)
                .addOnCanceledListener(() -> future.completeExceptionally(new CancellationException()));

        return future;
    }
}
