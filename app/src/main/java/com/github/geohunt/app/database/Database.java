package com.github.geohunt.app.database;

/**
 * This interface represents a NoSQL database abstraction representing Key-Value storage. It
 * can be used with FirebaseDatabaseWrapper, in order to use firebase as a backend
 */
public interface Database {
    /**
     * Returns a reference to the root location of the database.
     *
     * @return a DatabasePathReference object that represents a reference to the root location of the database.
     */
    DatabasePathReference root();
}
