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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.runBlocking
import kotlin.math.sin

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

    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }

    inner class VH(private val rowBinding: PostRowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {


        fun bind(holder: VH) {
            Log.d(javaClass.simpleName, "In post row adapter")
            val position = getPos(this)

            if (viewModel.getPublicPostsSize() != 0) {
                var auth = FirebaseAuth.getInstance()

                val postInfo = getItem(position)
                Log.d(javaClass.simpleName, "In here")

                runBlocking {
//                    viewModel.retrieveSongInfo(postInfo.songId)
                    val songInfo = viewModel.retrieveSongInfo(postInfo.songId)
                    Log.d(javaClass.simpleName, "In here")

                    rowBinding.songAuthor.text = songInfo.artist.name
                    rowBinding.songTitle.text = songInfo.title
                    rowBinding.postUsernameTextView.text = postInfo?.ownerName
                    rowBinding.postCaption.text = postInfo.caption

                    if (viewModel.getSongPlayingPos() == position) {
                        rowBinding.musicplayerPlayButton.setImageResource(R.drawable.baseline_pause_circle_24)

                    } else {
                        rowBinding.musicplayerPlayButton.setImageResource(R.drawable.ic_play_arrow_24)
                    }
                    rowBinding.musicplayerPlayButton.setOnClickListener {
                            // media player is playing current song so pause the song
                        Log.d("mediaPlayer", songInfo.toString())
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
                                    songInfo.preview.toUri()
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


                    // get image from firebase
                    if (postInfo.photoUuid.isNotEmpty()) {
                        viewModel.glideFetch(postInfo.photoUuid, rowBinding.postPhoto)

                    }

                    loadMostRecentImage(postInfo.ownerUid, rowBinding)

                    // check if user's UID is in the array of likes
                    if (postInfo.likes.contains(auth.currentUser?.uid)) {
                        rowBinding.postLikeButton.setImageResource(R.drawable.round_favorite_24)

                    } else {
                        rowBinding.postLikeButton.setImageResource(R.drawable.heart_button)

                    }

                    rowBinding.postLikesCountTextView.text = postInfo.likes.size.toString()

                    rowBinding.postLikeButton.setOnClickListener {
                        Log.d("likes listener", "likes array for ${postInfo.firestoreID} is : ${postInfo.likes}")

                        var liked = postInfo.likes.contains(auth.currentUser?.uid)
                        Log.d("likes", "user liked post: $liked ")

                        if (liked) {
                            // remove user from list of likes
                            viewModel.updateLikesInPost(postInfo.firestoreID, auth.currentUser?.uid!!, false) {
                                rowBinding.postLikeButton.setImageResource(R.drawable.heart_button)
                                Log.d(
                                    "remove like",
                                    "after removing likes array for ${postInfo.firestoreID} is : ${postInfo.likes}"
                                )
                                notifyItemChanged(position)
                            }

                        } else {

                            viewModel.updateLikesInPost(postInfo.firestoreID, auth.currentUser?.uid!!, true){
                                rowBinding.postLikeButton.setImageResource(R.drawable.round_favorite_24)
                                Log.d("add like", "after adding likes array for ${postInfo.firestoreID} is : ${postInfo.likes}")
                                notifyItemChanged(position)
                            }
                        }
                    }

                    // updateLikes(postId: String, userId: String, addLike: Boolean, callback:()->Unit = {})
                    // get song information

//                rowBinding.songAuthor.text = songInfo?.artist?.name
//                rowBinding.playerTimeRemainingText.text = toMinutesAndSeconds(songInfo?.duration!!)


                }
            }
        }
    }

    private fun loadMostRecentImage(uid: String, rowBinding: PostRowBinding) {
        val databaseReference = FirebaseStorage.getInstance().reference
        val recentImageRef = databaseReference.child("pfps").child("${uid}.jpg")

        recentImageRef.downloadUrl.addOnSuccessListener { uri ->
            viewModel.glideFetchPfp(uri.toString(), rowBinding.postUserPhoto)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = PostRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder)
    }
}

