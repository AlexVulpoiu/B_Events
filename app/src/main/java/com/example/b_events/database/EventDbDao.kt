package com.example.b_events.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDbDao {

    @Insert
    fun insert(eventDb: EventDb)

    @Query("SELECT * from events WHERE eventId = :key")
    fun get(key: Long): LiveData<EventDb>

    @Query("SELECT * FROM events WHERE user_id = :userId ORDER BY eventId DESC")
    fun getAllEvents(userId: String): LiveData<List<EventDb>>

    @Delete
    suspend fun deleteEvent(eventDb: EventDb)
}