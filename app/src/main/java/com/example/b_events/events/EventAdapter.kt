package com.example.b_events.events

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.b_events.EventItemViewHolder
import com.example.b_events.R
import com.example.b_events.databinding.EventItemViewBinding

class EventAdapter(private val clickListener: EventListener): RecyclerView.Adapter<EventItemViewHolder>() {
    var data = listOf<Event>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: EventItemViewHolder, position: Int) {
        val item = data[position]
        holder.viewDataBinding.also {
            it.event = item
            it.clickListener = clickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val withDataBinding: EventItemViewBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.event_item_view, parent, false)
        return EventItemViewHolder(withDataBinding)
    }
}

class EventListener(val clickListener: (eventUrl: String) -> Unit) {
    fun onClick(currentEvent: Event) = clickListener(currentEvent.link)
}