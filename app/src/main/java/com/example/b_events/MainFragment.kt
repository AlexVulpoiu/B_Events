/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.b_events

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

//        val textView = view.findViewById<TextView>(R.id.textView)
//        val button = view.findViewById<Button>(R.id.btnParseHTML)
//        button.setOnClickListener {
//            getHtmlFromWeb(textView)
//        }

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
//        binding.settingsButton.setOnClickListener {
//            val action = MainFragmentDirections.actionMainFragmentToSettingsFragment()
//            findNavController().navigate(action)
//        }
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

                    binding.authButton.text = getString(R.string.logout_button_text)
                    binding.authButton.setOnClickListener {
                        AuthUI.getInstance().signOut(requireContext())
                    }

                    binding.videoView.visibility = View.GONE
//                    binding.btnParseHTML.visibility = View.VISIBLE
//                    binding.textView.visibility = View.VISIBLE
                }
                else -> {
                    binding.welcomeText.text = getString(R.string.welcome_text)

                    binding.authButton.text = getString(R.string.login_button_text)
                    binding.authButton.setOnClickListener {
                        findNavController().navigate(R.id.loginFragment)
                    }
//                    binding.btnParseHTML.visibility = View.GONE
//                    binding.textView.visibility = View.GONE
                    binding.videoView.visibility = View.VISIBLE
                    binding.videoView.start()
                }
            }
        })
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

//    private fun getHtmlFromWeb(textView: TextView) {
//        Thread(Runnable {
//            val stringBuilder = StringBuilder()
//            try {
//                val doc: Document = Jsoup.connect("https://allevents.in/bucharest/all").get()
//                val events: Elements = doc.getElementsByClass("item event-item  box-link")
//                for (event in events) {
//                    val info = event.getElementsByClass("meta")
//                    val infoDiv = info[0].getElementsByClass("meta-right")
//                    val titleDiv = infoDiv[0].getElementsByClass("title")
//                    val title = titleDiv[0].child(0).child(0).ownText()
//                    val link = titleDiv[0].child(0).attr("href")
//                    val organiser = infoDiv[0].getElementsByClass("up-venue toh")[0].ownText()
//
//                    val dateDiv = info[0].getElementsByClass("meta-left")
//                    val month = dateDiv[0].getElementsByClass("up-month")[0].ownText()
//                    val day = dateDiv[0].getElementsByClass("up-day")[0].ownText()
//
//                    val imageDiv = event.getElementsByClass("thumb")
//                    val imageLink = imageDiv[0].attr("data-src")
//
//                    stringBuilder.append("\n").append("Title: ").append(title).append("\n")
//                        .append("Link: ").append(link).append("\n")
//                        .append("Month: ").append(month).append("\n")
//                        .append("Day: ").append(day).append("\n")
//                        .append("Organiser: ").append(organiser).append("\n")
//                        .append("Image: ").append(imageLink).append("\n")
//                }
//            } catch (e: IOException) {
//                stringBuilder.append("Error: ").append(e.message).append("\n")
//            }
//            activity?.runOnUiThread { textView.text = stringBuilder.toString() }
//        }).start()
//    }
}