package com.github.geohunt.app.model.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.geohunt.app.model.api.BoredActivity

/**
 * Bored API Data Access Object (DAO). This object is the interface with the Room database, and
 * allows various queries to be performed.
 */
@Dao
interface BoredDao {
    @Query("SELECT * FROM BoredActivity ORDER BY RAND() LIMIT 1")
    fun getRandomActivity(): BoredActivity

    @Insert
    fun addActivity(activity: BoredActivity)
}