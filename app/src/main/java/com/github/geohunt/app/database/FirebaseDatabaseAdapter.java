package com.github.geohunt.app.database;

import com.github.geohunt.app.utility.Conversions;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class FirebaseDatabaseAdapter implements Database {

    private final DatabasePathReference root;

    public FirebaseDatabaseAdapter(FirebaseDatabase firebaseDatabase) {
        this.root = new FirebaseDatabasePathAdapter(firebaseDatabase.getReference(), this);
    }

    @Override
    public DatabasePathReference root() {
        return root;
    }

    private static class FirebaseDatabasePathAdapter implements DatabasePathReference {
        private final FirebaseDatabaseAdapter rootDatabase;
        private final DatabaseReference firebaseDbRef;

        private FirebaseDatabasePathAdapter(DatabaseReference firebaseDbRef, FirebaseDatabaseAdapter rootDatabase) {
            this.firebaseDbRef = firebaseDbRef;
            this.rootDatabase = rootDatabase;
        }

        @Override
        public <T> CompletableFuture<T> getRequest(Class<T> classType) {
            return Conversions.taskToCompletableFuture(firebaseDbRef.get())
                    .thenCompose(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            try {
                                T t = dataSnapshot.getValue(classType);
                                return CompletableFuture.completedFuture(t);
                            } catch (DatabaseException e) {
                                CompletableFuture<T> future = new CompletableFuture<>();
                                future.completeExceptionally(e);
                                return future;
                            }
                        } else {
                            CompletableFuture<T> future = new CompletableFuture<>();
                            future.completeExceptionally(new NoSuchElementException());
                            return future;
                        }
                    });
        }

        @Override
        public CompletableFuture<Void> postRequest(Object object) {
            return Conversions.taskToCompletableFuture(firebaseDbRef.setValue(object));
        }

        @Override
        public DatabasePathReference child(String path) {
            return new FirebaseDatabasePathAdapter(firebaseDbRef.child(path), rootDatabase);
        }

        @Override
        public Database getDatabase() {
            return rootDatabase;
        }
    }
}
