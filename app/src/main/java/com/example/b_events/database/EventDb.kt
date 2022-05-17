package com.example.b_events.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventDb(
    @PrimaryKey(autoGenerate = true)
    var eventId: Long = 0,

    @ColumnInfo(name = "user_id")
    var userId: String = "",

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "link")
    var link: String = "",

    @ColumnInfo(name = "month")
    var month: String = "",

    @ColumnInfo(name = "day")
    var day: String = "",

    @ColumnInfo(name = "date_and_time")
    var dateAndTime: String = "",

    @ColumnInfo(name = "location_name")
    var locationName: String = "",

    @ColumnInfo(name = "full_location")
    var fullLocation: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "image_link")
    var imageLink: String = ""
)