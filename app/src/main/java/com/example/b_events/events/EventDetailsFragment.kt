package com.example.b_events.events

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.b_events.R
import com.example.b_events.databinding.FragmentEventDetailsBinding
import com.google.android.material.appbar.AppBarLayout

class EventDetailsFragment: Fragment() {

    private val eventViewModel by viewModels<EventViewModel>()

    private lateinit var binding: FragmentEventDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_details, container, false)

        arguments?.let { it ->
            val args = EventDetailsFragmentArgs.fromBundle(it)
            eventViewModel.getEvent(args.eventUrl) { eventIt ->
                eventIt.link = args.eventUrl
                binding.event = eventIt

                binding.mapsButtonEvent.setOnClickListener {
                    findNavController().navigate(
                        EventDetailsFragmentDirections
                            .actionEventDetailsFragmentToMapsFragment(eventIt.fullLocation)
                    )
                }
            }
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val appBarLayout: AppBarLayout = view.findViewById(R.id.appbar_layout)
        val motionLayout: MotionLayout = view.findViewById(R.id.motion_layout)

        val listener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            motionLayout.progress = seekPosition
        }

        appBarLayout.addOnOffsetChangedListener(listener)
    }

    private fun getShareIntent(): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val message = "Salut!\n" +
                "Tocmai am gasit un eveniment intersant in Bucuresti!\n" +
                "Este vorba despre ${binding.event?.title}!\n" +
                "Data: ${binding.event?.dateAndTime}\n" +
                "Locatia: ${binding.event?.fullLocation}\n" +
                "Pentru mai multe detalii acceseaza link-ul: ${binding.event?.link}.\n" +
                "Astept raspunsul tau!"
        shareIntent.setType("text/plain").putExtra(Intent.EXTRA_TEXT, message)
        return shareIntent
    }

    private fun shareEvent() {
        startActivity(getShareIntent())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.share_menu, menu)
        if(getShareIntent().resolveActivity(requireActivity().packageManager) == null) {
            menu.findItem(R.id.share).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share -> shareEvent()
        }
        return super.onOptionsItemSelected(item)
    }
}