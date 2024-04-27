package com.example.jamify

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jamify.model.PostMeta

enum class SortColumn {
    ASCENDING,
    DECENDING
}



class HomeViewModel : ViewModel() {
    // TODO: Implement the ViewModel


    //creating a mutable livedata list here as we are interested only in this user's
    //posts, we we keep track of that here
    private var postsMetaList = MutableLiveData<List<PostMeta>>()


}