package com.example.b_events.favorite_events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.b_events.R
import com.example.b_events.database.EventsDatabase
import com.example.b_events.databinding.FragmentFavoriteEventsBinding

class FavoriteEventsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentFavoriteEventsBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_favorite_events, container, false)
        val application = requireNotNull(this.activity).application

        val dataSource = EventsDatabase.getInstance(application).eventDbDao
        val viewModelFactory = FavoriteEventViewModelFactory(dataSource, application)

        val favoriteEventsViewModel = ViewModelProvider(this, viewModelFactory)[FavoriteEventViewModel::class.java]

        binding.favoriteEventViewModel = favoriteEventsViewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}