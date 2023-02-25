package com.github.geohunt.app.database;

import java.util.concurrent.CompletableFuture;

/**
 * This interface defines a reference to an abstract NoSQL Database, it can be
 * used to read, write to changes in data at that location.
 */
public interface DatabasePathReference {
    /**
     * Returns a CompletableFuture object that represents a pending operations to retrieve
     * a single object from the Database location referred to by this instance of DatabasePathReference
     * @param classType the type of the class to be returned
     * @return a CompletableFuture representing a pending operation to retrieve a single
     * object from the Database
     */
    <T> CompletableFuture<T> getRequest(Class<T> classType);

    /**
     * Posts a new object to the Database location referred to by this DatabasePathReference.
     * @param object the object to be posted to the Database.
     * @return a CompletableFuture representing a pending operation to post the object to the Database.
     */
    CompletableFuture<Void> postRequest(Object object);

    /**
     * Returns a DatabasePathReference object that represents a reference to the specified child location
     * of the Database location referred to by this DatabasePathReference.
     * @param path the path to the child location.
     * @return a DatabasePathReference object that represents a reference to the specified child location of the Database.
     */
    DatabasePathReference child(String path);

    /**
     * Returns a DatabasePathReference object that represent a reference to the Database
     * location specified by the given path
     * @param path the path to the Database key
     * @return a DatabasePathReference object that represents a reference to the specified Database location.
     */
    default DatabasePathReference path(String path) {
        if (path == null || path.isEmpty()) {
            return this;
        }

        String[] nodes = path.split("/");
        DatabasePathReference ref = this;
        for (String node : nodes) {
            if (!node.isEmpty()) {
                ref = ref.child(node);
            }
        }

        return ref;
    }

    /**
     * Returns the Database object associated with this DatabasePathReference.
     * @return the Database object associated with this DatabasePathReference.
     */
    Database getDatabase();
}
