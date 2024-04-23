package com.example.jamify

import androidx.lifecycle.ViewModel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.jamify.glide.Glide
import com.example.jamify.model.PostMeta
import com.example.jamify.view.TakePictureWrapper

class MainViewModel : ViewModel() {
    // Remember the uuid, and hence file name of file camera will create
    private var pictureUUID = ""
    // Only call this from TakePictureWrapper
    fun takePictureUUID(uuid: String) {
        pictureUUID = uuid
    }
    // LiveData for entire note list, all images
    private var postList = MutableLiveData<List<PostMeta>>()
    private var postsEmpty = MediatorLiveData<Boolean>().apply {
        addSource(postList) {
            this.value = it.isNullOrEmpty()
        }
    }
    // Remember what is expanded in NoteAdapter
    private var expandedMap = mutableMapOf<String,Boolean>()
    private var allImages = MutableLiveData<List<String>>()

    // Track current authenticated user
    private var currentAuthUser = invalidUser

    // Firestore state
    private val storage = Storage()
    // Database access
    private val dbHelp = ViewModelDBHelper()



    // create post data
    private var imageUpload = MutableLiveData<String>()
    private var caption = MutableLiveData<String>()
    private var songName = MutableLiveData<String>()


    // spotify authentication
    private var spotifyToken = MutableLiveData<String>()



    fun setSpotifyToken(token: String) {
        spotifyToken.value = token
    }


    fun observeSpotifyToken(): LiveData<String> {
        return spotifyToken
    }

    /////////////////////////////////////////////////////////////
    // Notes adapter.  With navigation, fragments are all
    // recycled aggressively, so state must live in viewModel
    fun isExpanded(position: Int) : Boolean {
        val id = postList.value?.get(position)?.firestoreID ?: ""
        return expandedMap[id] == true
    }
    fun isExpandable(position: Int) : Boolean {
        return postList.value?.get(position)?.photoUuid?.isNotEmpty() ?: false
    }
    fun toggleExpanded(position: Int) {
        if( isExpandable(position) ) {
            val id = postList.value?.get(position)?.firestoreID ?: ""
            expandedMap[id] = expandedMap[id] != true
        }
    }
    // MainActivity gets updates on this via live data and informs view model
    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

    /////////////////////////////////////////////////////////////
    // Notes, memory cache and database interaction
    fun fetchInitialNotes(callback: ()->Unit) {
        dbHelp.fetchInitialNotes(postList, callback)
    }
    fun observePosts(): LiveData<List<PostMeta>> {
        return postList
    }
    fun observePostsEmpty(): LiveData<Boolean> {
        return postsEmpty
    }
    // Get a post from the memory cache
    fun getPost(position: Int) :  PostMeta {
        val post = postList.value?.get(position)
        Log.d(javaClass.simpleName, "postList.value ${postList.value}")
        Log.d(javaClass.simpleName, "getNode $position list len ${postList.value?.size}")
        return post!!
    }
    // After we successfully modify the db, we refetch the contents to update our
    // live data.  Hence we always pass in notesList
    fun updatePost(position: Int, text: String, pictureUUIDs: List<String>) {
        val post = getPost(position)
        // Have to update text before calling updateNote
        post.caption = text

        dbHelp.updateNote(post, postList)
    }
    fun createNote(text: String, pictureUUID: String) {
        val post = PostMeta(
            ownerName = currentAuthUser.name,
            ownerUid = currentAuthUser.uid,
            photoUuid = pictureUUID,
            songTitle = "",
            caption = text
            // database sets firestoreID
        )
        dbHelp.createNote(post,postList)
    }
    fun removePostAt(position: Int) {
        //SSS
        val post = getPost(position)
        // Delete all pictures on the server, asynchronously
        storage.deleteImage(post.photoUuid)

        //EEE // XXX What do to before we delete note?
        Log.d(javaClass.simpleName, "remote note at pos: $position id: ${post.firestoreID}")
        dbHelp.removeNote(post, postList)
    }

    /////////////////////////////////////////////////////////////
    // Images
    private fun imageListReturns(pictureUUIDs: List<String>) {
        allImages.value = pictureUUIDs
    }
    // NB: Images are not sorted.  We need to add a timestamp if we want that
    fun refreshAllImages() {
        storage.listAllImages(::imageListReturns)
    }
    fun observeAllImages(): LiveData<List<String>> {
        return allImages
    }

    /////////////////////////////////////////////////////////////
    // We can't just schedule the file upload and return.
    // The problem is that our previous picture uploads can still be pending.
    // So a note can have a pictureFileName that does not refer to an existing file.
    // That violates referential integrity, which we really like in our db (and programming
    // model).
    // So we do not add the pictureFileName to the note until the picture finishes uploading.
    // That means a user won't see their picture updates immediately, they have to
    // wait for some interaction with the server.
    // You could imagine dealing with this somehow using local files while waiting for
    // a server interaction, but that seems error prone.
    // Freezing the app during an upload also seems bad.
    fun pictureSuccess(finished: (String)->Unit) {
        val photoFile = TakePictureWrapper.fileNameToFile(pictureUUID)
        //SSS
        // Upload, which deletes local file and finally our memory of its UUID
        storage.uploadImage(photoFile, pictureUUID) {
            finished(pictureUUID)
            pictureUUID = ""
        }
        //EEE // XXX Write me while preserving referential integrity
    }
    fun pictureFailure() {
        // Note, the camera intent will only create the file if the user hits accept
        // so I've never seen this called
        pictureUUID = ""
    }

    fun glideFetch(pictureUUID: String, imageView: ImageView) {
        Glide.fetch(storage.uuid2StorageReference(pictureUUID),
            imageView)
    }

    /////////////////////////////////////////////////////////////
    // Create Post Data





}