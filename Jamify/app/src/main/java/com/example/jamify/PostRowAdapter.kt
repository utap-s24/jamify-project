package com.example.jamify

import android.app.Activity
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jamify.databinding.PostRowBinding
import com.example.jamify.model.PostMeta
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking

/**
 * Created by witchel on 8/25/2019
 */

// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
//
// You can call adapterPosition to get the index of the selected item
class PostRowAdapter(val context: Activity, private val viewModel: MainViewModel)
    : ListAdapter<PostMeta, PostRowAdapter.VH> (Diff()) {
    class Diff: DiffUtil.ItemCallback<PostMeta>() {

        override fun areItemsTheSame(oldItem: PostMeta, newItem: PostMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }
        override fun areContentsTheSame(oldItem: PostMeta, newItem: PostMeta): Boolean {
            return oldItem.caption == newItem.caption
        }

    }

    inner class VH(private val rowBinding: PostRowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {


        fun bind(holder: VH, position: Int) {
            Log.d(javaClass.simpleName, "In post row adapter")
            if (viewModel.getPublicPostsSize() != 0) {
                var auth = FirebaseAuth.getInstance()

                val postInfo = getItem(position)
                runBlocking {
                    viewModel.retrieveSongInfo(postInfo.songId)
                    rowBinding.songAuthor.text = viewModel.loadedSongInfo?.value?.artist?.name
                    rowBinding.songTitle.text = viewModel.loadedSongInfo?.value?.title
                    rowBinding.postUsernameTextView.text = auth.currentUser?.displayName
                    rowBinding.postCaption.text = postInfo.caption

                    val mediaPlayer = MediaPlayer.create(
                        context,
                        viewModel.loadedSongInfo?.value?.preview?.toUri()
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


                    // get image from firebase
                    if (postInfo.photoUuid.isNotEmpty()) {
                        viewModel.glideFetch(postInfo.photoUuid, rowBinding.postPhoto)

                    }

                    // get song information

//                rowBinding.songAuthor.text = songInfo?.artist?.name
//                rowBinding.playerTimeRemainingText.text = toMinutesAndSeconds(songInfo?.duration!!)


                }
            }
        }
    }

    fun toMinutesAndSeconds(duration: Int): String {
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return "$minutes:$seconds"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = PostRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)
    }
}

