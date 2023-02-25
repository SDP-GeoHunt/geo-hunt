package com.github.geohunt.app;

import com.github.geohunt.app.database.Database;
import com.github.geohunt.app.database.DatabasePathReference;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class MockDatabase implements Database {
    private static final String PATH_SEPARATOR = "/";

    private final BiFunction<String, Class<?>, CompletableFuture<Object>> getRequestHandler;
    private final BiFunction<String, Object, CompletableFuture<Void>> postRequestHandler;
    private final MockDatabasePathReference root = new MockDatabasePathReference("");

    public MockDatabase(BiFunction<String, Class<?>, CompletableFuture<Object>> getRequestHandler, BiFunction<String, Object, CompletableFuture<Void>> postRequestHandler) {
        this.getRequestHandler = getRequestHandler;
        this.postRequestHandler = postRequestHandler;
    }


    @Override
    public DatabasePathReference root() {
        return root;
    }

    private class MockDatabasePathReference implements DatabasePathReference {

        private final String fullPath;

        private MockDatabasePathReference(String fullPath) {
            this.fullPath = fullPath;
        }

        public String getFullPath() {
            return fullPath;
        }

        @Override
        public <T> CompletableFuture<T> getRequest(Class<T> classType) {
            return getRequestHandler.apply(fullPath, classType)
                    .thenApply(obj -> (T) obj);
        }

        @Override
        public CompletableFuture<Void> postRequest(Object object) {
            return postRequestHandler.apply(fullPath, object);
        }

        @Override
        public DatabasePathReference child(String path) {
            return new MockDatabasePathReference(fullPath + PATH_SEPARATOR + path);
        }

        @Override
        public Database getDatabase() {
            return MockDatabase.this;
        }
    }
}
