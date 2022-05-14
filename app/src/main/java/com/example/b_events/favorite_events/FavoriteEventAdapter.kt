package com.example.b_events.favorite_events

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.b_events.FavoriteEventItemViewHolder
import com.example.b_events.R
import com.example.b_events.database.EventDb
import com.example.b_events.databinding.FavoriteEventItemViewBinding
import java.util.*
import kotlin.collections.ArrayList

class FavoriteEventAdapter(private var eventsList: ArrayList<EventDb>,
                           private val clickListener: FavoriteEventListener,
                           private val favoriteEventViewModel: FavoriteEventViewModel)
    : RecyclerView.Adapter<FavoriteEventItemViewHolder>(), Filterable {

//    var data = listOf<EventDb>()
//        @SuppressLint("NotifyDataSetChanged")
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }

    var data = ArrayList<EventDb>()

    init {
        data = eventsList
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FavoriteEventItemViewHolder, position: Int) {
        val item = data[position]
        holder.viewDataBinding.also {
            it.favoriteEvent = item
            it.clickListener = clickListener
            it.favoriteEventViewModel = favoriteEventViewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val withDataBinding: FavoriteEventItemViewBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.favorite_event_item_view, parent, false)
        return FavoriteEventItemViewHolder(withDataBinding)
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                data = if(charSearch.isNotBlank()) {
                    val resultList = ArrayList<EventDb>()
                    for(ev in data) {
                        if(ev.title.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
                            resultList.add(ev)
                        }
                    }
                    resultList
                } else {
                    eventsList
                }

                val filterResults = FilterResults()
                filterResults.values = data
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                data = results?.values as ArrayList<EventDb>
                notifyDataSetChanged()
            }
        }
    }
}

class FavoriteEventListener(val clickListener: (favoriteEventId: Long) -> Unit) {
    fun onClick(favoriteEvent: EventDb) = clickListener(favoriteEvent.eventId)
}