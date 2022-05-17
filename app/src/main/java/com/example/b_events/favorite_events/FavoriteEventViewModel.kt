package com.example.b_events.favorite_events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.b_events.database.EventDb
import com.example.b_events.database.EventDbDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class FavoriteEventViewModel(val database: EventDbDao, application: Application, favoriteEventId: Long = 0L):
    AndroidViewModel(application) {

    private val _navigateToFavoriteEventDetail = MutableLiveData<Long?>()
    val navigateToFavoriteEventDetail
        get() = _navigateToFavoriteEventDetail

    private val favoriteEvent: LiveData<EventDb> = database.get(favoriteEventId)
    fun getFavoriteEvent() = favoriteEvent

    private val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val favoriteEventsList: LiveData<List<EventDb>> = database.getAllEvents(user)
    fun getFavoriteEvents() = favoriteEventsList

    fun addToFavorites(eventUrl: String) {

        var title: String
        var description: String
        var fullLocation: String
        var dateAndTime: String
        var day: String
        var imageLink: String

        viewModelScope.launch(Dispatchers.IO) {
            val doc: Document = Jsoup.connect(eventUrl).get()
            title = doc.getElementsByClass("overlay-h1 toh")[0].ownText()
            description = doc.getElementsByClass("event-description-html")[0].wholeText()
            fullLocation = doc.getElementsByClass("full-venue")[0].ownText()
            dateAndTime = doc.getElementsByClass("convert_time")[0].ownText()
            day = doc.getElementsByClass("display_start_time convert_date_format")[0].ownText()
            imageLink = doc.getElementsByClass("event-thumb check_img hidden-phone")[0].attr("src")

            val newEvent = EventDb()
            newEvent.title = title
            newEvent.description = description
            newEvent.fullLocation = fullLocation
            newEvent.dateAndTime = dateAndTime
            newEvent.day = day
            newEvent.link = eventUrl
            newEvent.imageLink = imageLink
            newEvent.userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            insert(newEvent)
        }
    }

    private fun insert(event: EventDb) {

        var eventExists = false
        for(ev in FavoriteEventsFragment.favoriteEventsList) {
            if(ev.title == event.title
                && ev.dateAndTime == event.dateAndTime
                && ev.fullLocation == event.fullLocation) {
                eventExists = true
            }
        }
        if(!eventExists) {
            database.insert(event)
        }
    }

    fun removeFromFavorites(event: EventDb) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteEvent(event)
        }
    }

    private suspend fun deleteEvent(event: EventDb) {
        database.deleteEvent(event)
    }

    fun onFavoriteEventClicked(id: Long) {
        _navigateToFavoriteEventDetail.value = id
    }

    fun onFavoriteEventDetailNavigated() {
        _navigateToFavoriteEventDetail.value = null
    }
}