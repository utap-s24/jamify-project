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

// Pass in a function called clickListener that takes a view and a songName
// as parameters.  Call clickListener when the row is clicked.
class DataAdapter(val context: Activity,
                    private val viewModel: MainViewModel,
                    private val clickListener: (songIndex: Int) -> Unit
)
// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
    : ListAdapter<Data,
        DataAdapter.ViewHolder>(Diff())
{
    companion object {
        val TAG = "RVDiffAdapter"
    }

    // ViewHolder pattern holds row binding
    inner class ViewHolder(val songRowBinding : SongCardBinding)
        : RecyclerView.ViewHolder(songRowBinding.root) {
        init {
            //XXX Write me.
            itemView.setOnClickListener {
                // TODO: not sure about this either
                var prevSongSelected = viewModel.selectedIndex
//                viewModel.selectedIndex = bindingAdapterPosition
                viewModel.selectedIndex = bindingAdapterPosition

                clickListener(bindingAdapterPosition)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //XXX Write me.
        val rowBinding = SongCardBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //XXX Write me.
//        holder.
//        holder.itemView.setOnClickListener {
//            clickListener(position)
//        }


        val currentData = viewModel.getCopyOfSongInfo()?.get(position)


        // dependency used to load image

        val rowBinding = holder.songRowBinding


        if (currentData != null) {
            Picasso.get().load(currentData.album.cover).into(rowBinding.musicImage)

            rowBinding.songAuthor.text = currentData.artist?.name
            rowBinding.songTitle.text = currentData?.title

            val mediaPlayer = MediaPlayer.create(
                context,
                currentData.preview.toUri()
            )
            rowBinding.musicplayerPlayButton.setOnClickListener {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    rowBinding.musicplayerPlayButton.setImageResource(R.drawable.ic_play_arrow_24)
                } else {
                    mediaPlayer.start()
                    rowBinding.musicplayerPlayButton.setImageResource(R.drawable.baseline_pause_circle_24)
                }
            }

        }
    }

    class Diff : DiffUtil.ItemCallback<Data>() {
        // Item identity
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.title == newItem.title
        }
    }
}

