package com.github.geohunt.app;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.github.geohunt.app.database.Database;
import com.github.geohunt.app.database.DatabasePathReference;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class DatabasePathReferenceTest {
    private static class MockDbPathReference implements DatabasePathReference {

        public final String fullPath;

        private MockDbPathReference(String fullPath) {
            this.fullPath = fullPath;
        }

        @Override
        public <T> CompletableFuture<T> getRequest(Class<T> classType) {
            return null;
        }

        @Override
        public CompletableFuture<Void> postRequest(Object object) {
            return null;
        }

        @Override
        public DatabasePathReference child(String path) {
            return new MockDbPathReference(fullPath + "." + path);
        }

        @Override
        public Database getDatabase() {
            return null;
        }
    }

    public String fullPath(DatabasePathReference dpr) {
        return ((MockDbPathReference) dpr).fullPath;
    }

    @Test
    public void testPathReturnsSelfWhenEmptyInput() {
        DatabasePathReference ref = new MockDbPathReference("root");
        assertThat(ref.path(null), is(ref));
        assertThat(ref.path(""), is(ref));
    }

    @Test
    public void testPathReturnsChildWithOneLevel() {
        DatabasePathReference ref = new MockDbPathReference("root");
        assertThat(fullPath(ref.path("HelloWorld")), equalTo("root.HelloWorld"));
        assertThat(fullPath(ref.path("This is goodbye")), equalTo("root.This is goodbye"));
    }

    @Test
    public void testPathReturnsGrandChildWithMultipleLevels() {
        DatabasePathReference ref = new MockDbPathReference("root");
        assertThat(fullPath(ref.path("HelloWorld/HowAreYou")), equalTo("root.HelloWorld.HowAreYou"));
        assertThat(fullPath(ref.path("This/is/goodbye")), equalTo("root.This.is.goodbye"));
    }
}
