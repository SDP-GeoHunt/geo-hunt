package com.github.geohunt.app.utility;

import com.google.android.gms.tasks.Task;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class Conversions {
    private Conversions() {
    }

    /**
     * This static method is used to convert a Task<T> to a CompletableFuture<T>.
     *
     * @param task the Task<T> to be converted to a CompletableFuture<T>.
     * @return a CompletableFuture<T> object that represents the same task as the input parameter.
     * @param <T> the type of the underlying object being produced by the Task
     */
    public static <T> CompletableFuture<T> taskToCompletableFuture(Task<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();

        task.addOnSuccessListener(future::complete)
                .addOnFailureListener(future::completeExceptionally)
                .addOnCanceledListener(() -> future.completeExceptionally(new CancellationException()));

        return future;
    }
}
