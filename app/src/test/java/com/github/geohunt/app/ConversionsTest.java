package com.github.geohunt.app;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.geohunt.app.utility.Conversions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ConversionsTest {
    @Test
    public void taskToFutureWorkUponSuccess() {
        MockTask task = new MockTask();
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Conversions.taskToCompletableFuture(task)
                .thenAccept(atomicInteger::set)
                .exceptionally(ignored -> {
                    Assert.fail();
                    return null;
                });
        task.succeed(150);

        assertThat(atomicInteger.get(), equalTo(150));
    }

    @Test
    public void taskToFutureWorkUponFailure() {
        MockTask task = new MockTask();
        RuntimeException exception = new RuntimeException("ExpectedMockException");
        AtomicInteger atomicInteger = new AtomicInteger(0);
        AtomicReference<Throwable> exceptionAtomicReference = new AtomicReference<>();

        Conversions.taskToCompletableFuture(task)
                .thenAccept(atomicInteger::set)
                .exceptionally(e -> {
                    exceptionAtomicReference.set(e.getCause());
                    return null;
                });
        task.failed(exception);

        assertThat(exceptionAtomicReference.get(), is(exception));
        assertThat(atomicInteger.get(), equalTo(0));
    }

    @Test
    public void taskToFutureWorkUponCancelled() {
        MockTask task = new MockTask();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        AtomicReference<Throwable> exceptionAtomicReference = new AtomicReference<>();

        Conversions.taskToCompletableFuture(task)
                .thenAccept(atomicInteger::set)
                .exceptionally(e -> {
                    exceptionAtomicReference.set(e.getCause());
                    return null;
                });
        task.cancel();

        assertThat(exceptionAtomicReference.get().getClass(), equalTo(CancellationException.class));
        assertThat(atomicInteger.get(), equalTo(0));
    }

    private static class MockTask extends Task<Integer> {
        private List<OnFailureListener> onFailureListeners = new ArrayList<>();
        private List<OnSuccessListener<? super Integer>> onSuccessListeners = new ArrayList<>();
        private List<OnCanceledListener> onCanceledListeners = new ArrayList<>();

        public void succeed(int value) {
            onSuccessListeners
                    .forEach(onSuccessListener -> onSuccessListener.onSuccess(value));
        }

        public void failed(Exception e) {
            onFailureListeners
                    .forEach(onFailureListener -> onFailureListener.onFailure(e));
        }

        public void cancel() {
            onCanceledListeners
                    .forEach(OnCanceledListener::onCanceled);
        }

        @NonNull
        @Override
        public Task<Integer> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            onFailureListeners.add(onFailureListener);
            return this;
        }

        @NonNull
        @Override
        public Task<Integer> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            onFailureListeners.add(onFailureListener);
            return this;
        }

        @NonNull
        @Override
        public Task<Integer> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            onFailureListeners.add(onFailureListener);
            return this;
        }

        @NonNull
        @Override
        public Task<Integer> addOnSuccessListener(@NonNull OnSuccessListener<? super Integer> onSuccessListener) {
            onSuccessListeners.add(onSuccessListener);
            return this;
        }

        @NonNull
        @Override
        public Task<Integer> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Integer> onSuccessListener) {
            onSuccessListeners.add(onSuccessListener);
            return this;
        }

        @NonNull
        @Override
        public Task<Integer> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Integer> onSuccessListener) {
            onSuccessListeners.add(onSuccessListener);
            return this;
        }

        @NonNull
        @Override
        public Task<Integer> addOnCanceledListener(@NonNull OnCanceledListener onCanceledListener) {
            onCanceledListeners.add(onCanceledListener);
            return this;
        }

        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public Integer getResult() {
            return 50;
        }

        @Override
        public <X extends Throwable> Integer getResult(@NonNull Class<X> aClass) throws X {
            return 50;
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public boolean isComplete() {
            return false;
        }

        @Override
        public boolean isSuccessful() {
            return false;
        }
    }

    ;
}
