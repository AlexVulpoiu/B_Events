package com.example.b_events.favorite_events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.b_events.R
import com.example.b_events.database.EventDb
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

        var adapter = FavoriteEventAdapter(ArrayList(), FavoriteEventListener {
            favoriteEventsViewModel.onFavoriteEventClicked(it)
        }, favoriteEventsViewModel)

        favoriteEventsViewModel.getAllFavoriteEvents { favoriteEvents ->
            adapter =
                FavoriteEventAdapter(favoriteEvents as ArrayList<EventDb>, FavoriteEventListener {
                    favoriteEventsViewModel.onFavoriteEventClicked(it)
                }, favoriteEventsViewModel)

            binding.favoriteEventsList.adapter = adapter
            binding.favoriteEventViewModel = favoriteEventsViewModel
            binding.lifecycleOwner = this
        }

        favoriteEventsViewModel.navigateToFavoriteEventDetail.observe(viewLifecycleOwner) {
            it?.let {
                this.findNavController()
                    .navigate(
                        FavoriteEventsFragmentDirections
                        .actionFavoriteEventsFragmentToFavoriteEventDetailsFragment(it)
                    )
                favoriteEventsViewModel.onFavoriteEventDetailNavigated()
            }
        }

        binding.eventSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })

        return binding.root
    }
}