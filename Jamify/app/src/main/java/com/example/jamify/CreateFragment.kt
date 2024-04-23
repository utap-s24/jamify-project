package com.example.jamify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.jamify.databinding.FragmentCreateBinding
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

/**
 * A fragment representing a list of Items.
 */
class CreateFragment : Fragment() {
    companion object {
        fun newInstance() = CreateFragment()
    }

    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentCreateBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val PICK_IMAGE_REQUEST = 1
// Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private val REQUEST_CODE = 1337;
    private val REDIRECT_URI = "http://com.example.jamify/callback"
    private val clientId = "7608f3b5605e4cf0bd9e5c49da401627"
    private val redirectUri = "http://com.example.jamify/callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type="image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
//        val builder = AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
//
//        builder.setScopes(arrayOf("streaming"));
//        val request = builder.build();
//
//        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request);

        val childTrans =childFragmentManager.beginTransaction()
        childTrans.add(R.id.musicContainer, SpotifyPlayer())
        binding.image.setOnClickListener {
            openFileChooser()
        }

        // connect to spotify once authenticated
//        viewModel.observeSpotifyToken().observe(viewLifecycleOwner) {
//            Log.d(javaClass.simpleName, "Spotify token: $it")
//            val connectionParams = ConnectionParams.Builder(clientId)
//                .setRedirectUri(redirectUri)
//                .showAuthView(true)
//                .build()
//
//            SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
//                override fun onConnected(appRemote: SpotifyAppRemote) {
//                    spotifyAppRemote = appRemote
//                    Log.d("MainActivity", "Connected! Yay!")
//                    // Now you can start interacting with App Remote
//                    connected()
//                }
//
//                override fun onFailure(throwable: Throwable) {
//                    Log.e("MainActivity", throwable.message, throwable)
//                    // Something went wrong when attempting to connect! Handle errors here
//                }
//            })
//        }





    }

    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            binding.image.setImageURI(imageUri)
        }


        // Check if result comes from the correct activity

        // Check if result comes from the correct activity
//        if (requestCode === REQUEST_CODE) {
//            val response = AuthorizationClient.getResponse(resultCode, data)
//            when (response.type) {
//                AuthorizationResponse.Type.TOKEN -> {
//                    Log.d(javaClass.simpleName, "SUCCESS authorizing Spotify account")
//                    viewModel.setSpotifyToken(response.accessToken)
//                }
//                AuthorizationResponse.Type.ERROR -> {
//                    Log.d(javaClass.simpleName, "Error authorizing Spotify account")
//
//                }
//                else -> {}
//            }
//        }
    }
}