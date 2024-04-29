package com.example.jamify.com.example.jamify

import android.app.Activity
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
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
//            songRowBinding.musicplayerPlayButton.setOnClickListener {
//                clickListener(bindingAdapterPosition)
//            }

            itemView.setOnClickListener {
                // TODO: not sure about this either
                var prevSongSelected = viewModel.selectedIndex
//                viewModel.selectedIndex = bindingAdapterPosition
                viewModel.selectedIndex = bindingAdapterPosition

                clickListener(bindingAdapterPosition)
            }
        }
    }

    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = SongCardBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentData = viewModel.getCopyOfSongInfo()?.get(position)
        val rowBinding = holder.songRowBinding

        if (currentData != null) {
            viewModel.observeSongPlayingPos().observe(context as LifecycleOwner) { playingPos ->
                if (playingPos != position) {
                    // Reset button state to play
                    rowBinding.musicplayerPlayButton.setImageResource(R.drawable.ic_play_arrow_24)
                }
            }

            if (viewModel.getSongPlayingPos() == position) {
                rowBinding.musicplayerPlayButton.setImageResource(R.drawable.baseline_pause_circle_24)

            } else {
                rowBinding.musicplayerPlayButton.setImageResource(R.drawable.ic_play_arrow_24)
            }
            rowBinding.songTitle.text = currentData.title

            rowBinding.songAuthor.text = currentData.artist.name
                // Load song information
            if (currentData.album.cover != "") {
                Picasso.get().load(currentData.album.cover).into(rowBinding.musicImage)
            }


            rowBinding.musicplayerPlayButton.setOnClickListener {
                // media player is playing current song so pause the song
                Log.d("mediaPlayer", currentData.toString())
                Log.d("mediaPlayer", rowBinding.songTitle.text.toString())
                if (viewModel.mediaPlayer.isPlaying && viewModel.getSongPlayingPos() == position) {
                    viewModel.mediaPlayer.pause()
                    rowBinding.musicplayerPlayButton.setImageResource(R.drawable.ic_play_arrow_24)
                    Log.d("mediaPlayer", " in first IF")
                    // media player was originally playing a diff song so initialize mediaplayer with the current song
                    // and update song index in viewModel
                } else if (viewModel.getSongPlayingPos() != position) {
                    Log.d("mediaPlayer", " in ELSE IF, tyring to stop")
                    if(viewModel.mediaPlayer.isPlaying){
                        viewModel.mediaPlayer.stop()
                    }
                    viewModel.mediaPlayer = MediaPlayer.create(
                        context,
                        currentData.preview.toUri()
                    )
                    viewModel.mediaPlayer.start()
                    val originalSongIndex = viewModel.getSongPlayingPos()
                    rowBinding.musicplayerPlayButton.setImageResource(R.drawable.baseline_pause_circle_24)
                    viewModel.setSongPlayingPos(position)

                    if (originalSongIndex != -1) {
                        notifyItemChanged(originalSongIndex)
                    }

                } else if (!viewModel.mediaPlayer.isPlaying && viewModel.getSongPlayingPos() == position){
                    Log.d("mediaPlayer", " in 2nd ELSE IF, tyring to stop")

                    viewModel.mediaPlayer.start()
                    rowBinding.musicplayerPlayButton.setImageResource(R.drawable.baseline_pause_circle_24)
                }

            }

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