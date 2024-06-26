package com.example.jamify

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel

import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.example.jamify.com.example.jamify.APIInterface
import com.example.jamify.com.example.jamify.SongsRepository
import com.example.jamify.glide.Glide
import com.example.jamify.glide.GlideApp
import com.example.jamify.model.PostMeta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.util.UUID


data class SortInfo(val ascending: Boolean)
class MainViewModel(application: Application) : AndroidViewModel(application){
    // Remember the uuid, and hence file name of file camera will create
    private var pictureUUID = ""
    // Only call this from TakePictureWrapper
    fun takePictureUUID(uuid: String) {
        pictureUUID = uuid
    }
    // Track current authenticated user
    private var auth =  FirebaseAuth.getInstance()

    // Firestore state
    private var storage = Storage()
    // Database access
    private val dbHelp = ViewModelDBHelper()
    // LiveData for entire note list, all images

    // LiveData for entire posts list, all public posts
    private var postList = MutableLiveData<List<PostMeta>>()
    private var profilePhotoUpdating = MutableLiveData<Boolean>().apply { this.value = false }
    private var postsEmpty = MediatorLiveData<Boolean>().apply {
        addSource(postList) {
            this.value = it.isNullOrEmpty()
        }
    }
    val placeholderSong = "https://cdns-preview-5.dzcdn.net/stream/c-56d349f2423e3b3da5c2eeecca1ab512-13.mp3".toUri()

//    var mediaPlayer: MediaPlayer = MediaPlayer.create(
//            application.applicationContext,
//            placeholderSong
//    )
    var mediaPlayer = MediaPlayer()

    private var filterSongTitle = MutableLiveData<String>()
    private var sortInfo = MutableLiveData(
        SortInfo(false))
    private var songPlayingPos = MutableLiveData<Int>(-1)


    // Post List of Current users posts
    private var userPosts = MediatorLiveData<List<PostMeta>>().apply{
        // XXX Write me, viewModelScope.launch getSubreddits()
        //filter based on user id
        addSource(postList) {
            viewModelScope.launch() {
                value = getUserPosts()
            }
        }
    }



    private var filteredUserPosts = MediatorLiveData<List<PostMeta>>().apply{
        // XXX Write me, viewModelScope.launch getSubreddits()
        //filter based on user id
        addSource(userPosts) {
            viewModelScope.launch() {
                value = filterPostsList()
            }
        }

        addSource(filterSongTitle) {
            viewModelScope.launch() {
                value = filterPostsList()
            }
        }
    }

    fun observeSortInfo(): LiveData<SortInfo> {
        return sortInfo
    }

    fun sortInfoClick(sortColumn: String,
                      resultListener: () -> Unit) {
        // XXX User has changed sort info
        // Update `sortInfo` with the new sorting criteria
        var ascending = true
        if (sortColumn == "Newest") {
            //if its the same column reverse ascending argument
            ascending = false
        }

        sortInfo.value = SortInfo(ascending)
        // Fetch the sorted note list from the database
        fetchInitialPosts(resultListener)
    }


    private var publicPosts = MediatorLiveData<List<PostMeta>>().apply {
        addSource((postList)) {
            viewModelScope.launch() {
                value = filterPublicPosts()
            }

        // XXX Write me, viewModelScope.launch getPosts
        }
    }
    // Remember what is expanded in NoteAdapter
    private var expandedMap = mutableMapOf<String,Boolean>()
    private var allImages = MutableLiveData<List<String>>()




    private var api : APIInterface= APIInterface.create()
    private var repository = SongsRepository(api)
    // create post data
    var songId = MutableLiveData<Long>()
    var songName = MutableLiveData<String>()
    var selectedIndex = 0

    private var searchedSongs = MutableLiveData<List<Data>>()

    private var profileImageUpload = MutableLiveData<Uri>()
    private var profileImageFile = MutableLiveData<File>()

    private var imageUpload = MutableLiveData<Uri>()
    private var imageFile = MutableLiveData<File>()
    private var caption = MutableLiveData<String>()
    private var postPrivacy = MutableLiveData<Boolean>(true)

    // home fragment data
    var loadedSongInfo =  MutableLiveData<SongInfo>()
    fun setSongPlayingPos(index : Int) {
        songPlayingPos.value = index
    }

    fun observeSongPlayingPos() : LiveData<Int> {
        return songPlayingPos
    }

    fun getSongPlayingPos() : Int {
        return songPlayingPos.value!!
    }

    private fun filterPublicPosts():List<PostMeta>? {
        return postList.value?.filter {
            !it.private
        }
    }

    fun setSongId(id: Long) {
        Log.d(javaClass.simpleName, "setSongId $id")
        songId.value = id
    }

    fun setSearchedSongs(songs: List<Data>) {
        searchedSongs.value = songs
    }

    fun getCopyOfSongInfo(): List<Data>? {
        return searchedSongs.value
    }

    fun getSong(index: Int): Data {
        return searchedSongs.value!![index]
    }

    suspend fun retrieveSongInfo(id: Long) : SongInfo{
        return repository.retrieveSongInfo(id)!!
    }

    suspend fun retrieveSongs(searchTerm: String) {
       searchedSongs.value = repository.retrieveSearchedSongs(searchTerm)

    }

    fun setSelectedImage(image: Uri) {
        imageUpload.value = image
    }

    fun setSelectedProfileImage(image: Uri) {
        profileImageUpload.value = image
    }



//    val mutableUserPosts = MutableLiveData<List<PostMeta>>()
//
//    postList.observeForever { posts ->
//        val userPosts = posts.filter { it.ownerUid == userId }
//        mutableUserPosts.value = userPosts
//    }

    private fun getUserPosts():List<PostMeta>? {
//        if (filterSongTitle.value == null) return userPosts.value
//        removeAllCurrentSpans()
        val userId = auth.currentUser?.uid!!
        return postList.value?.filter {
            it.searchForUser(userId)
        }


    }

    fun updateLikesInPost(postId: String, userId: String, liked: Boolean, callback:()->Unit = {}) {
        dbHelp.updateLikes(userId, liked, postId, callback)
        val postIndex = postList.value?.indexOfFirst { it.firestoreID == postId }
        postIndex?.let { index ->
            val updatedPost = postList.value?.get(index)?.copy(
                likes = if (liked) {
                    postList.value?.get(index)?.likes?.plus(userId)
                } else {
                    postList.value?.get(index)?.likes?.minus(userId)
                } ?: emptyList()
            )
            updatedPost?.let {
                val updatedPostList = postList.value!!.toMutableList()
                updatedPostList[index]= updatedPost
                postList.value = updatedPostList
            }
        }
    }

    private fun filterPostsList():List<PostMeta>? {
        if (filterSongTitle.value == "---") return userPosts.value
//        removeAllCurrentSpans()
        return userPosts.value?.filter {
            it.searchForSong(filterSongTitle.value.toString())
        }
    }
    /////////////////////////////////////////////////////////////
    // Notes adapter.  With navigation, fragments are all
    // recycled aggressively, so state must live in viewModel
    fun isExpanded(position: Int) : Boolean {
        val id = postList.value?.get(position)?.firestoreID ?: ""
        return expandedMap[id] == true
    }

    fun getCurrentAuthUser(): FirebaseUser? {
        return auth.currentUser
    }

    /////////////////////////////////////////////////////////////
    // Notes, memory cache and database interaction
    fun fetchInitialPosts(callback: ()->Unit) {
        dbHelp.fetchInitialNotes(postList, sortInfo.value!!, callback)

    }

    fun observePublicPosts(): LiveData<List<PostMeta>> {
        Log.d(javaClass.simpleName, "observePublicPosts " + publicPosts.value)
        return publicPosts
    }

    fun observePosts(): LiveData<List<PostMeta>> {
        Log.d(javaClass.simpleName, "observePosts " + postList.value)
        return postList
    }
    fun observePostsEmpty(): LiveData<Boolean> {
        return postsEmpty
    }

    // Function to extract unique song titles from user's posts
    fun extractSongTitles(): Set<String> {
        Log.d("UserPost", "CALLING THIS METHOD" )
        Log.d("UserPost", userPosts.value?.size.toString() )


        val songTitles = mutableSetOf<String>()
        userPosts.value?.forEach { post ->
            Log.d("UserPost", "${post.ownerName}, ${post.songTitle}" )
            Log.d("UserPost", post.songTitle )
            songTitles.add(post.songTitle)
        }

        return songTitles
    }

    fun observeUserPosts():MediatorLiveData<List<PostMeta>> {
        return userPosts
    }

    fun setSongFilter(songTitle: String){
        Log.d("song filter", "selected: ${songTitle}")
        filterSongTitle.value = songTitle

        Log.d("song filter", "filterSongTitle = ${filterSongTitle.value.toString()}")

    }
    fun observeFilteredUserPosts(): LiveData<List<PostMeta>> {
        return filteredUserPosts
    }

    fun observeProfilePhotoUpdating(): LiveData<Boolean> {
        return profilePhotoUpdating
    }

    fun setProfilePhotoUpdating(updating: Boolean){
        profilePhotoUpdating.value = updating
        Log.d("pfp", "updating = true")

    }

    fun setPrivacy(private: Boolean) {
        // XXX Write me
        postPrivacy.value = private
    }
    fun getPublicPostsSize(): Int {
        return postList.value?.size ?: 0
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

        dbHelp.updateNote(post, postList, sortInfo.value!!)
    }

    fun setImageFile(file: File) {
        imageFile.value = file
    }

    fun setProfileImageFile(file: File) {
        Log.d("pfp", "file name: ${file.name}" )
        profileImageFile.value = file
    }



    fun getImageFile() : File {
        return imageFile.value!!
    }

    fun getProfileImageFile() : File {
        return profileImageFile.value!!
    }

    fun getImageURI(): Uri? {
        return imageUpload.value
    }

    // Create a post and upload image to storage
    fun createNote(text: String) {
        Log.d(javaClass.simpleName, "currentAuthUser.name ${auth.currentUser?.displayName}")
        Log.d(javaClass.simpleName, "currentAuthUser.uid ${auth.currentUser?.uid}")
        Log.d(javaClass.simpleName, "song ID ${songId.value}")
        val post=
            PostMeta(
                ownerName = auth.currentUser?.displayName!!,
                ownerUid = auth.currentUser?.uid!!,
                photoUuid = imageFile.value?.name!!,
                songTitle = songName.value!!,
                songId= songId.value!!,
                private = postPrivacy.value!!,
                caption = text
            // database sets firestoreID
        )
        dbHelp.createNote(post,postList, sortInfo.value!!)

    }
    fun removePostAt(position: Int) {
        //SSS
        val post = getPost(position)
        // Delete all pictures on the server, asynchronously
        storage.deleteImage(post.photoUuid)

        //EEE // XXX What do to before we delete note?
        Log.d(javaClass.simpleName, "remote note at pos: $position id: ${post.firestoreID}")
        dbHelp.removeNote(post, postList, sortInfo.value!!)

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
    fun pictureSuccess() {
//        val photoFile = TakePictureWrapper.fileNameToFile(imageUpload.value ?: "")
        //SSS
        // Upload, which deletes local file and finally our memory of its UUID

        storage.uploadImage(imageUpload.value!!, imageFile.value!!) {
            imageUpload.value = Uri.EMPTY
            imageFile.value = File("")
        }

        //EEE // XXX Write me while preserving referential integrity
    }

    fun pfpSuccess(uuid: String) {
        storage.uploadPfp(profileImageUpload.value!!, profileImageFile.value!!, uuid) {
            profileImageUpload.value = Uri.EMPTY
            profileImageFile.value = File("")
        }
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
    fun glideFetchPfp(uri: String, imageView: ImageView) {
        Glide.fetchProfile(uri,
            imageView)
        profilePhotoUpdating.value = false
    }

    /////////////////////////////////////////////////////////////
    // Create Post Dat
    fun uploadImageToFirebase(context: Context, imageUri: Uri, imageView: ImageView) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef = storageRef.child("pfps/${auth.currentUser?.uid.toString()}.jpg")

        imagesRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                // You can get the download URL of the image
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    imageView.setImageURI(imageUri)
                    // Now you can use the download URL as needed
                }.addOnFailureListener {
                    // Handle any errors getting the download URL
                    imageView.setImageResource(R.drawable.baseline_account_circle_24)

                }
            }
            .addOnFailureListener { exception ->
                // Handle unsuccessful uploads
                imageView.setImageResource(R.drawable.baseline_account_circle_24)
            }
    }
}
