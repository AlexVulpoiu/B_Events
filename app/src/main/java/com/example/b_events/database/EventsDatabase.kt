package com.example.b_events.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EventDb::class], version = 2, exportSchema = false)
abstract class EventsDatabase: RoomDatabase() {

    abstract val eventDbDao: EventDbDao

    companion object {

        @Volatile
        private var INSTANCE: EventsDatabase? = null

        fun getInstance(context: Context): EventsDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        EventsDatabase::class.java, "events_database")
                        .fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}