package com.github.geohunt.app.api

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Represents the Room database for Bored Activities storage.
 */
@Database(entities = [BoredActivityData::class], version = 1)
abstract class BoredDatabase: RoomDatabase() {
    abstract fun activityDao(): BoredDao
}