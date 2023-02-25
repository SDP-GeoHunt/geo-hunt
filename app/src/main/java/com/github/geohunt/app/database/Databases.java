package com.github.geohunt.app.database;

import com.google.firebase.database.FirebaseDatabase;

public class Databases {
    private Databases() {}

    private static Database database = null;

    /**
     * Retrieve the instance of the database used throughout the program
     *
     * @return The database to be used by the application
     */
    public static Database getInstance() {
        if (database == null) {
            database = new FirebaseDatabaseWrapper(FirebaseDatabase.getInstance());
        }
        return database;
    }

    /**
     * Set the instance of the database to be used (for testing purpose)
     *
     * @param db the database to be used
     */
    public static void setInstance(Database db) {
        database = db;
    }
}
