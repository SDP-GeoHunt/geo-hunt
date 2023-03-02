package com.github.geohunt.app.database;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Databases {
    private Databases() {}

    private static volatile Database instance = null;
    private static final Object mutex = new Object();

    /**
     * Retrieve the instance of the database used throughout the program
     *
     * @return The database to be used by the application
     */
    public static Database getInstance() {
        Database result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    instance = result = new FirebaseDatabaseAdapter(FirebaseDatabase.getInstance());
                }
            }
        }
        return result;
    }

    /**
     * Set the instance of the database to be used (for testing purpose)
     *
     * @param db the database to be used
     */
    public static void setInstance(Database db) {
        Objects.requireNonNull(db);
        synchronized (mutex) {
            instance = db;
        }
    }
}
