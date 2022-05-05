package com.example.b_events.events

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

    fun getEvents(cb: (List<Event>) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            val eventsList: MutableList<Event> = mutableListOf()
            val doc: Document = Jsoup.connect("https://allevents.in/bucharest/all").get()
            val events: Elements = doc.getElementsByClass("item event-item  box-link")

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
                val imageLink = imageDiv[0].attr("data-src")

                val currentEvent = Event()
                currentEvent.title = title
                currentEvent.link = link
                currentEvent.month = month
                currentEvent.day = day
                currentEvent.organiser = organiser
                currentEvent.imageUrl = imageLink

                eventsList.add(currentEvent)
            }

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    cb.invoke(eventsList)
                }
            }
        }
    }
}
