package com.github.geohunt.app.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class FirebaseDatabaseWrapper implements Database {

    private final FirebaseDatabase firebaseDatabase;
    private final DatabasePathReference root;

    public FirebaseDatabaseWrapper(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
        this.root = new FirebaseDatabasePathWrapper(firebaseDatabase.getReference(), this);
    }

    @Override
    public DatabasePathReference root() {
        return root;
    }

    private static class FirebaseDatabasePathWrapper implements DatabasePathReference {
        private final FirebaseDatabaseWrapper rootDatabase;
        private final DatabaseReference firebaseDbRef;

        private FirebaseDatabasePathWrapper(DatabaseReference firebaseDbRef, FirebaseDatabaseWrapper rootDatabase) {
            this.firebaseDbRef = firebaseDbRef;
            this.rootDatabase = rootDatabase;
        }

        @Override
        public <T> CompletableFuture<T> getRequest(Class<T> classType) {
            CompletableFuture<T> completableFuture = new CompletableFuture<>();

            // Convert the android-specific task to a more general CompletableFuture
            firebaseDbRef.get()
                    .addOnSuccessListener(it -> {
                        if (it.exists()) {
                            T t = it.getValue(classType);
                            completableFuture.complete(t);
                        }
                        else {
                            completableFuture.completeExceptionally(new NoSuchElementException());
                        }
                    })
                    .addOnFailureListener(completableFuture::completeExceptionally);

            return completableFuture;
        }

        @Override
        public CompletableFuture<Void> postRequest(Object object) {
            CompletableFuture<Void> completableFuture = new CompletableFuture<>();

            // Convert the android-specific task to a more general CompletableFuture
            firebaseDbRef.setValue(object)
                    .addOnSuccessListener(it -> {
                        completableFuture.complete(null);
                    })
                    .addOnFailureListener(completableFuture::completeExceptionally);

            return completableFuture;
        }

        @Override
        public DatabasePathReference child(String path) {
            return new FirebaseDatabasePathWrapper(firebaseDbRef.child(path), rootDatabase);
        }

        @Override
        public Database getDatabase() {
            return rootDatabase;
        }
    }
}
