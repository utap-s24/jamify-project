package com.example.jamify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jamify.databinding.PostRowBinding
import com.example.jamify.model.PostMeta

// Pass in a function called clickListener that takes a view and a songName
// as parameters.  Call clickListener when the row is clicked.
class RVDiffAdapter(private val viewModel: MainViewModel,
                    private val clickListener: (songIndex : Int)->Unit)
// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
    : ListAdapter<PostMeta,
        RVDiffAdapter.ViewHolder>(Diff())
{
    companion object {
        val TAG = "RVDiffAdapter"
    }

    private fun getPos(holder: ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }

    // ViewHolder pattern holds row binding
    inner class ViewHolder(val postRowBinding: PostRowBinding)
        : RecyclerView.ViewHolder(postRowBinding.root) {
        init {
            //XXX Write me.
            // Set the click listener for the row
            //TODO: EXPAND ROW TO VIEW FULL POST

//            itemView.setOnClickListener{
//                val position = getPos(this)
//                clickListener(position)
//            }

        }

        fun bind(holder: ViewHolder, position: Int) {
            val postMeta = viewModel.getPost(position)
            viewModel.glideFetch(postMeta.photoUuid, postRowBinding.postPhoto)
            //todo: finish binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //XXX Write me.
        val postRowBinding = PostRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(postRowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //XXX Write me.
        val rowBinding = holder.postRowBinding
        val post = getItem(position)
        rowBinding.songTitle.text = post.songTitle

        //if it is current song set background to yellow
//        if(position == viewModel.currentIndex) {
//            Log.d("RVDiffAdaptor", "Song position ${position}, curSongIndex: ${viewModel.currentIndex}")
//            holder.itemView.setBackgroundColor(Color.YELLOW) //https://stackoverflow.com/questions/31842983/getresources-getcolor-is-deprecated
//        } else {
//            holder.itemView.setBackgroundColor(Color.TRANSPARENT) //https://stackoverflow.com/questions/31842983/getresources-getcolor-is-deprecated
//        }

    }

    class Diff : DiffUtil.ItemCallback<PostMeta>() {
        // Item identity
        override fun areItemsTheSame(oldItem: PostMeta, newItem: PostMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: PostMeta, newItem: PostMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.caption == newItem.caption
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.ownerName == newItem.ownerName
                    && oldItem.photoUuid == newItem.photoUuid
                    && oldItem.byteSize == newItem.byteSize
                    && oldItem.timeStamp == newItem.timeStamp
        }
    }
}

