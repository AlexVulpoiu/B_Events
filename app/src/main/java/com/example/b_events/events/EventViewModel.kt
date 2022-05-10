package com.example.b_events.events

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class EventViewModel: ViewModel() {

    private val _navigateToEventDetail = MutableLiveData<String?>()
    val navigateToEventDetail
        get() = _navigateToEventDetail

    fun getEvents(cb: (List<Event>) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            val eventsList: MutableList<Event> = mutableListOf()
            val doc: Document = Jsoup.connect("https://allevents.in/bucharest/all").get()
            val events: Elements = doc.getElementsByClass("item event-item  box-link")

            var first = true
            for (event in events) {
                val info = event.getElementsByClass("meta")
                val infoDiv = info[0].getElementsByClass("meta-right")
                val titleDiv = infoDiv[0].getElementsByClass("title")
                val title = titleDiv[0].child(0).child(0).ownText()
                val link = titleDiv[0].child(0).attr("href")
                val organiser = infoDiv[0].getElementsByClass("up-venue toh")[0].ownText()

                val dateDiv = info[0].getElementsByClass("meta-left")
                val month = dateDiv[0].getElementsByClass("up-month")[0].ownText()
                val day = dateDiv[0].getElementsByClass("up-day")[0].ownText()

                val imageDiv = event.getElementsByClass("thumb")
                var imageLink = imageDiv[0].attr("data-src")

                if(first) {
                    imageLink = imageDiv[0].attr("style")
                    val startPos = imageLink.indexOf("(")
                    val endPos = imageLink.indexOf(")")
                    imageLink = imageLink.substring(startPos + 1, endPos)
                    first = false
                }

                val currentEvent = Event()
                currentEvent.title = title
                currentEvent.link = link
                currentEvent.month = month
                currentEvent.day = day
                currentEvent.locationName = organiser
                currentEvent.imageLink = imageLink

                eventsList.add(currentEvent)
            }

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    cb.invoke(eventsList)
                }
            }
        }
    }

    fun getEvent(url: String, cb: (Event) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            val doc: Document = Jsoup.connect(url).get()
            val title: String = doc.getElementsByClass("overlay-h1 toh")[0].ownText()
            val description: String = doc.getElementsByClass("event-description-html")[0].wholeText()
            val fullLocation: String = doc.getElementsByClass("full-venue")[0].ownText()
            val dateAndTime: String = doc.getElementsByClass("convert_time")[0].ownText()
            val day: String = doc.getElementsByClass("display_start_time convert_date_format")[0].ownText()
            val imageLink: String = doc.getElementsByClass("event-thumb check_img hidden-phone")[0].attr("src")

            val event = Event()
            event.title = title
            event.description = description
            event.fullLocation = fullLocation
            event.dateAndTime = dateAndTime
            event.day = day
            event.imageLink = imageLink

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    cb.invoke(event)
                }
            }
        }
    }

    fun onEventClicked(eventLink: String) {
        _navigateToEventDetail.value = eventLink
    }

    fun onEventDetailNavigated() {
        _navigateToEventDetail.value = null
    }
}
