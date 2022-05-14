package com.example.b_events.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDbDao {

    @Insert
    fun insert(eventDb: EventDb)

    @Query("SELECT * from events WHERE eventId = :key")
    fun get(key: Long): LiveData<EventDb>

    @Query("SELECT * FROM events ORDER BY eventId DESC")
    fun getAllEvents(): List<EventDb>
}