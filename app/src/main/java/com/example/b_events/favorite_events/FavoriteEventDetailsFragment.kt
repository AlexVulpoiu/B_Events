package com.example.b_events.favorite_events

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.b_events.R
import com.example.b_events.database.EventsDatabase
import com.example.b_events.databinding.FragmentFavoriteEventDetailsBinding
import com.google.android.material.appbar.AppBarLayout

class FavoriteEventDetailsFragment: Fragment() {

    private lateinit var binding: FragmentFavoriteEventDetailsBinding

    private lateinit var favoriteEventViewModel: FavoriteEventViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val application = requireNotNull(this.activity).application
        val dataSource = EventsDatabase.getInstance(application).eventDbDao

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_event_details, container, false)

        arguments?.let { it ->
            val args = FavoriteEventDetailsFragmentArgs.fromBundle(it)
            favoriteEventViewModel = FavoriteEventViewModel(dataSource, application, args.favoriteEventId)
            val currentEvent = favoriteEventViewModel.getFavoriteEvent()
            currentEvent.observe(viewLifecycleOwner) {
                binding.favoriteEvent = currentEvent.value
            }

            binding.mapsFavButton.setOnClickListener {
                findNavController().navigate(
                    FavoriteEventDetailsFragmentDirections
                        .actionFavoriteEventDetailsFragmentToMapsFragment(currentEvent.value!!.fullLocation)
                )
            }
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val appBarLayout: AppBarLayout = view.findViewById(R.id.fav_appbar_layout)
        val motionLayout: MotionLayout = view.findViewById(R.id.fav_motion_layout)

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
                "Este vorba despre ${binding.favoriteEvent?.title}!\n" +
                "Data: ${binding.favoriteEvent?.dateAndTime}\n" +
                "Locatia: ${binding.favoriteEvent?.fullLocation}\n" +
                "Pentru mai multe detalii acceseaza link-ul: ${binding.favoriteEvent?.link}.\n" +
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