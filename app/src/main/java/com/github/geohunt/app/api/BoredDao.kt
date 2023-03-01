package com.github.geohunt.app.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Bored API Data Access Object (DAO). This object is the interface with the Room database, and
 * allows various queries to be performed.
 */
@Dao
interface BoredDao {
    @Query("SELECT * FROM BoredActivityData ORDER BY RAND() LIMIT 1")
    fun getRandomActivity(): BoredActivityData

    @Insert
    fun addActivity(activity: BoredActivityData)
}