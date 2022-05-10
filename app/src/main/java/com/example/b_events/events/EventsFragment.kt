package com.example.b_events.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.b_events.R
import com.example.b_events.databinding.FragmentEventsBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth

class EventsFragment : Fragment() {

    // Get a reference to the ViewModel scoped to this Fragment
    private val eventViewModel by viewModels<EventViewModel>()

    private lateinit var binding: FragmentEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_events, container, false)
        binding.eventViewModel = eventViewModel

        val adapter = EventAdapter(EventListener {
            eventViewModel.onEventClicked(it)
        })

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
