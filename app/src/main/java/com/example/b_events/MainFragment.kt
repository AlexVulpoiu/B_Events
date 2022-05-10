package com.example.b_events

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.b_events.authentication.LoginViewModel
import com.example.b_events.databinding.FragmentMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class MainFragment : Fragment() {

    // Get a reference to the ViewModel scoped to this Fragment
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()

        val videoView = view.findViewById<VideoView>(R.id.videoView)
        //Creating MediaController
        val mediaController = MediaController(activity)
        mediaController.setAnchorView(videoView)
        //specify the location of media file
        val uri: Uri = Uri.parse("android.resource://com.example.b_events/" + R.raw.bucharest)
        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
    }

    /**
     * Observes the authentication state and changes the UI accordingly.
     * If there is a logged in user: (1) show a logout button and (2) display their name.
     * If there is no logged in user: show a login button
     */
    private fun observeAuthenticationState() {
        val factToDisplay = viewModel.getFactToDisplay(requireContext())

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    binding.welcomeText.text = getFactWithPersonalization(factToDisplay)
                    binding.authButton.alpha = 1F
                    binding.authButton.text = getString(R.string.logout_button_text)
                    binding.authButton.setOnClickListener {
                        AuthUI.getInstance().signOut(requireContext())
                    }

                    binding.videoView.visibility = View.GONE
                }
                else -> {
                    binding.authButton.alpha = 0F
                    binding.welcomeText.text = getString(R.string.welcome_text)

                    fade(binding.authButton)
                    binding.authButton.text = getString(R.string.login_button_text)
                    binding.authButton.setOnClickListener {
                        findNavController().navigate(R.id.loginFragment)
                    }
                    binding.videoView.visibility = View.VISIBLE
                    binding.videoView.start()
                }
            }
        })
    }

    private fun fade(obj: View) {

        val animator = ObjectAnimator.ofFloat(obj, View.ALPHA, 1f)
        animator.duration = 2500
        animator.startDelay = 3000
        animator.repeatCount = 0
        animator.start()
    }

    @SuppressLint("StringFormatMatches")
    private fun getFactWithPersonalization(fact: String): String {
        return String.format(
            resources.getString(
                R.string.welcome_message_authed,
                FirebaseAuth.getInstance().currentUser?.displayName,
                Character.toLowerCase(fact[0]) + fact.substring(1)
            )
        )
    }
}