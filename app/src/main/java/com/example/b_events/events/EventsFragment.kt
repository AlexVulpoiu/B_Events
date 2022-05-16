package com.example.b_events.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.b_events.R
import com.example.b_events.authentication.LoginViewModel
import com.example.b_events.database.EventsDatabase
import com.example.b_events.databinding.FragmentEventsBinding
import com.example.b_events.favorite_events.FavoriteEventViewModel
import com.example.b_events.favorite_events.FavoriteEventViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class EventsFragment : Fragment() {

    private val TAG = "EventsFragment"

    // Get a reference to the ViewModel scoped to this Fragment
    private val eventViewModel by viewModels<EventViewModel>()

    private val loginViewModel by viewModels<LoginViewModel>()

    private lateinit var binding: FragmentEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_events, container, false)
        binding.eventViewModel = eventViewModel

        val application = requireNotNull(this.activity).application
        val dataSource = EventsDatabase.getInstance(application).eventDbDao
        val viewModelFactory = FavoriteEventViewModelFactory(dataSource, application)
        val favoriteEventViewModel = ViewModelProvider(this, viewModelFactory)[FavoriteEventViewModel::class.java]

        val adapter = EventAdapter(EventListener {
            eventViewModel.onEventClicked(it)
        }, favoriteEventViewModel)

        eventViewModel.getEvents {
            adapter.data = it
        }
        binding.eventsList.adapter = adapter

//        binding.lifecycleOwner = this

        binding.welcomeUser.text = getFactWithPersonalization()

        eventViewModel.navigateToEventDetail.observe(viewLifecycleOwner) {
            it?.let {
                this.findNavController()
                    .navigate(EventsFragmentDirections.actionEventsFragmentToEventDetailsFragment(it))
                eventViewModel.onEventDetailNavigated()
            }
        }

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
                    Toast.makeText(context, "You should be authenticated to view the events", Toast.LENGTH_LONG).show()
                }
                else -> Log.e(
                    TAG, "New $authenticationState state that doesn't require any UI change"
                )
            }
        })
    }

    @SuppressLint("StringFormatMatches")
    private fun getFactWithPersonalization(): String {
        return String.format(
            resources.getString(
                R.string.welcome_message_authed,
                FirebaseAuth.getInstance().currentUser?.displayName
            )
        )
    }
}
