package com.example.b_events.favorite_events

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.b_events.R
import com.example.b_events.authentication.LoginViewModel
import com.example.b_events.database.EventDb
import com.example.b_events.database.EventsDatabase
import com.example.b_events.databinding.FragmentFavoriteEventsBinding

class FavoriteEventsFragment: Fragment() {

    private val TAG = "FavoriteEventsFragment"

    private val loginViewModel by viewModels<LoginViewModel>()

    companion object {
        lateinit var favoriteEventsList: List<EventDb>
    }

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

        favoriteEventsViewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            adapter =
                FavoriteEventAdapter(favoriteEvents as ArrayList<EventDb>, FavoriteEventListener {
                    favoriteEventsViewModel.onFavoriteEventClicked(it)
                }, favoriteEventsViewModel)

            favoriteEventsList = favoriteEvents
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> Log.i(TAG, "Authenticated")
                // If the user is not logged in, they should not be able to set any preferences,
                // so navigate them to the login fragment
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> {
                    navController.navigate(
                        R.id.loginFragment
                    )
                    Toast.makeText(context, "You should be authenticated to view favorite events", Toast.LENGTH_LONG).show()
                }
                else -> Log.e(
                    TAG, "New $authenticationState state that doesn't require any UI change"
                )
            }
        })
    }
}