package com.example.jamify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jamify.com.example.jamify.APIInterface
import com.example.jamify.com.example.jamify.DataAdapter
import com.example.jamify.databinding.FragmentCreateBinding
import com.google.common.io.Files.getFileExtension
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Long
import android.provider.OpenableColumns
import androidx.navigation.fragment.findNavController
import java.net.URI
import java.util.UUID

/**
 * A fragment representing a list of Items.
 */
class CreateFragment : Fragment() {
    companion object {
        fun newInstance() = CreateFragment()
    }
    private val viewModel: MainViewModel by viewModels()

    // XXX initialize viewModel
    private var _binding: FragmentCreateBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val PICK_IMAGE_REQUEST = 1

    lateinit var recyclerView: RecyclerView
    lateinit var myAdapter: DataAdapter

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

        recyclerView = binding.recyclerView
        myAdapter = DataAdapter(requireActivity(), viewModel, ::onClickListener)


        // populate adapter with list of songs
//        val retrofitBuilder = Retrofit.Builder()
//            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(APIInterface::class.java)
//
//        val retrofitData = retrofitBuilder.getData("eminem")
//
//        retrofitData.enqueue(object : Callback<MyData?> {
//            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
//                val responseBody = response.body()?.data!!
//                myAdapter = DataAdapter(activity!!, responseBody)
//                recyclerView.adapter = myAdapter
//                recyclerView.layoutManager = LinearLayoutManager(context)
////                val textView = binding.response
////                textView.text = responseBody.toString()
//                Log.d("TAG: onResponse", "onResponse: "  + responseBody.toString())
//
//            }
//
//            override fun onFailure(call: Call<MyData?>, t: Throwable) {
//                // if api call is failure then this method is executed
//                Log.e("MainActivity", "onFailure: " + t.message)
//            }
//        })
        binding.image.setOnClickListener {
            openFileChooser()
        }


        binding.search.setOnClickListener {
            val searchTerm = binding.searchText.text.toString()
            if (searchTerm.isNotEmpty()) {
                Log.e("MainActivity", searchTerm)
                binding.indeterminateBar.visibility = View.VISIBLE
                searchSong(searchTerm)
            }
        }

        binding.switchPrivacy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.setPrivacy(false)
            } else {
                viewModel.setPrivacy(true)
            }
        }
        // on pressing create, will upload image to firebase storage and will create firebase doc
        binding.create.setOnClickListener {
            if (viewModel.songName.value != "") {
                if (viewModel.getImageFile().isFile()){
                    // upload image to firebase storage
                    // create firebase doc

                    viewModel.pictureSuccess()
                    Log.d("CreateFragment", viewModel.getCurrentAuthUser().toString())

                    // TODO: update view model list of posts in feed and private view
                    viewModel.createNote(binding.captionText.text.toString())
                    viewModel.songId.value = -1
                    viewModel.songName.value = ""
                    binding.captionText.text.clear()
                    binding.image.setImageResource(R.drawable.baseline_image_search_24)
                    findNavController().navigate(R.id.homeFragment)
                }
            }
            // otherwise, show popup to prompt user to select a song
        }
    }

    fun searchSong(searchTerm: String) : Unit {

        binding.response.visibility = View.GONE
        binding.selectedSongTitle.visibility = View.GONE

        viewModel.retrieveSongs(searchTerm)
        myAdapter.submitList(viewModel.getCopyOfSongInfo())
        recyclerView.visibility = View.VISIBLE
        binding.indeterminateBar.visibility = View.GONE
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
//                val textView = binding.response
//                textView.text = responseBody.toString()
        Log.d("TAG: onResponse", "onResponse: setting searched songs")

    }
    fun onClickListener(index: Int) : Unit {
        Log.d("CreateFragment", "onClickListener")
        viewModel.songName.value = viewModel.getSong(index).title
        Log.d("DataAdapter", "Song name: ${viewModel.getSong(index).id}")
        viewModel.setSongId(viewModel.getSong(index).id)
//                clickListener(bindingAdapterPosition)
        recyclerView.visibility = View.GONE
//        notifyItemChanged(bindingAdapterPosition)
        viewModel.selectedIndex = index
        viewModel.songName.value = viewModel.getSong(index).title
        viewModel.setSongId(viewModel.getSong(index).id)


        binding.response.visibility = View.VISIBLE
        binding.response.text = viewModel.songName.value
        binding.selectedSongTitle.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri = data?.data // Get the URI of the selected file
            val file = getFileFromUri(requireContext(), uri!!)
            if (file != null) {
                viewModel.setImageFile(file)
            }
            binding.image.setImageURI(uri)
            viewModel.setSelectedImage(uri!!)
        }
    }



private fun getFileName(): String? {
//    var result: String? = null
//    val cursor = context?.contentResolver?.query(uri, null, null, null, null)
//        cursor?.use {
//        if (cursor.moveToFirst()) {
//            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            result = cursor.getString(nameIndex)
//        }
//    }
//    private fun generateFileName(): String {
//        return UUID.randomUUID().toString()

    return UUID.randomUUID().toString()
}

    fun getFileFromUri(context: Context, uri: Uri): File? {
        var inputStream: InputStream? = null
        var file: File? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val fileName = getFileName()
            val fileExtension = getFileExtension(uri)
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            file = File.createTempFile(fileName, "${fileName}.jpg", storageDir)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return file
    }

    fun getFileExtension(uri: Uri) : String {
        val mimeType  = context?.contentResolver?.getType(uri)
        return mimeType?.substringAfter("/") ?: ""
    }
}