package com.example.jamify.com.example.jamify

import android.app.Activity
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jamify.Data
import com.example.jamify.MainViewModel
import com.example.jamify.MyData
import com.example.jamify.R
import com.example.jamify.databinding.SongCardBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class DataAdapter(
    val context: Activity,
    private val viewModel: MainViewModel,
    private val clickListener: (songIndex: Int) -> Unit
) : ListAdapter<Data, DataAdapter.ViewHolder>(Diff()) {

    inner class ViewHolder(val songRowBinding: SongCardBinding) :
        RecyclerView.ViewHolder(songRowBinding.root) {

        init {
            // Click listener for the play button
            songRowBinding.musicplayerPlayButton.setOnClickListener {
                clickListener(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = SongCardBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentData = viewModel.getCopyOfSongInfo()?.get(position)
        val rowBinding = holder.songRowBinding

        if (currentData != null) {
            // Load song information
            // (Assuming Picasso.get().load(currentData.album.cover) is used to load the song cover image)

            // Set click listener for the play/pause button
            // holder.toggleMediaPlayer()
        }
    }

    class Diff : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.title == newItem.title
        }
    }
}
//class DataAdapter(
//    val context: Activity,
//    private val viewModel: MainViewModel,
//    private val clickListener: (songIndex: Int) -> Unit
//) : ListAdapter<Data, DataAdapter.ViewHolder>(Diff()) {
//
//    private var mediaPlayer: MediaPlayer? = null
//    private var lastPlayedPosition: Int = -1
//
//    inner class ViewHolder(val songRowBinding: SongCardBinding) :
//        RecyclerView.ViewHolder(songRowBinding.root) {
//
//        init {
//            // Click listener for the play button
//            songRowBinding.musicplayerPlayButton.setOnClickListener {
//                val currentPosition = bindingAdapterPosition
//                if (currentPosition == lastPlayedPosition) {
//                    // Toggle play/pause if the same song is clicked again
//                    toggleMediaPlayer()
//                } else {
//                    // Stop the previously playing media player, if any
//                    stopMediaPlayer()
//                    // Start playing the clicked media player
//                    startMediaPlayer(currentPosition)
//                }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val rowBinding = SongCardBinding.inflate(LayoutInflater.from(parent.context))
//        return ViewHolder(rowBinding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val currentData = viewModel.getCopyOfSongInfo()?.get(position)
//        val rowBinding = holder.songRowBinding
//
//        if (currentData != null) {
//            // Load song information
//            // (Assuming Picasso.get().load(currentData.album.cover) is used to load the song cover image)
//
//            // Set click listener for the play/pause button
//            rowBinding.musicplayerPlayButton.setOnClickListener {
//                toggleMediaPlayer()
//            }
//        }
//    }
//
//    private fun startMediaPlayer(position: Int) {
//        val currentData = viewModel.getCopyOfSongInfo()?.get(position)
//        if (currentData != null) {
//            mediaPlayer = MediaPlayer.create(context, currentData.preview.toUri())
//            mediaPlayer?.start()
//            lastPlayedPosition = position
//        }
//    }
//
//    private fun stopMediaPlayer() {
//        mediaPlayer?.apply {
//            if (isPlaying) {
//                stop()
//                release()
//            }
//        }
//        mediaPlayer = null
//        lastPlayedPosition = -1
//    }
//
//    private fun toggleMediaPlayer() {
//        if (mediaPlayer != null) {
//            if (mediaPlayer?.isPlaying == true) {
//                mediaPlayer?.pause()
//            } else {
//                mediaPlayer?.start()
//            }
//        }
//    }
//
//    class Diff : DiffUtil.ItemCallback<Data>() {
//        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
//            return oldItem.title == newItem.title
//        }
//    }
//}