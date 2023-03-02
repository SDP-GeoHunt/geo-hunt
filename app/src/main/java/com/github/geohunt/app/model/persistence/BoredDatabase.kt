package com.github.geohunt.app.model.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.geohunt.app.model.api.BoredActivity

/**
 * Represents the Room database for Bored Activities storage.
 */
@Database(entities = [BoredActivity::class], version = 1)
abstract class BoredDatabase: RoomDatabase() {
    abstract fun activityDao(): BoredDao
}