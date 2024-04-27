package com.example.jamify

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.jamify.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID


class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    //variables
    private val PICK_IMAGE_REQUEST = 1
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth


    private val viewModel: MainViewModel by activityViewModels()

    //posts
    private lateinit var adapter: RVDiffAdapter

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type="image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

    }

//    private fun initAdapter(binding: FragmentRvBinding) {
    private fun initAdapter(binding: FragmentProfileBinding) {
        val postRowAdapter = PostRowAdapter(requireActivity(), viewModel)
        binding.postsRecyclerView.adapter = postRowAdapter
        // XXX Write me, observe posts
        viewModel.observeFilteredUserPosts().observe(viewLifecycleOwner) {
            if (it != null) {
                println(it.size)
                postRowAdapter.submitList(it)
                postRowAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Clear menu because we don't want settings icon
//                menu.
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Do nothing
                return false
            }
        }, viewLifecycleOwner)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.fetchInitialPosts(){}
        viewModel.observeUserPosts().observe(viewLifecycleOwner) { userPosts ->
            // Extract unique song titles from user's posts
            val songTitles = viewModel.extractSongTitles().toMutableList()
            songTitles.add(0, "---") // Add "No Filter" as the default selection

            val filterSpinner = binding.filterSpinner
            val filterAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, songTitles)
            filterAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            filterSpinner.adapter = filterAdapter

            binding.filterSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    Log.d("Profile", "pos $position")

                    Log.d("Profile", songTitles.toString())
                    viewModel.setSongFilter(songTitles[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Log.d("Profile", "onNothingSelected")
                }
            }

            binding.postsCountTextView.text = userPosts.size.toString() // size will be 0 if the list is null


        }

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        val currentUser = firebaseAuth.currentUser


        // XXX Write me.
        initAdapter(binding)
        val songTitles = viewModel.extractSongTitles().toTypedArray()



        binding.usernameTextView.text = currentUser?.displayName


        val sortBy = arrayListOf<String>("Newest", "Oldest")
        val sortSpinner = binding.sortSpinner
        val filterAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, sortBy)
        filterAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = filterAdapter
//
        binding.sortSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View?, // Change View to View?
                                        position: Int, id: Long) {
                // Check if view is not null before using it
                view?.let {
                    Log.d("Profile", "pos $position")

                    Log.d("Profile", songTitles.toString())
                    //                viewModel.setSongFilter(songTitles[position])
                    viewModel.sortInfoClick(sortBy[position]){}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("Profile", "onNothingSelected")
            }
        }

        viewModel.glideFetchPfp("${firebaseAuth.currentUser?.uid.toString()}.jpg", binding.pofilePicImageView)

        binding.pofilePicImageView.setOnClickListener {
            openFileChooser()
        }

        binding.logoutButton.setOnClickListener {
            if (currentUser != null) {
                firebaseAuth.signOut()
                //after successful sign up end this activity
                val intent = Intent(requireActivity(), LogInActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                    //nav to Log in


            }
        }
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

        return firebaseAuth.currentUser?.uid!!
    }
    fun getFileExtension(uri: Uri) : String {
        val mimeType  = context?.contentResolver?.getType(uri)
        return mimeType?.substringAfter("/") ?: ""
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            val uri = data?.data // Get the URI of the selected file
            val file = getFileFromUri(requireContext(), uri!!)
            if (file != null) {
                viewModel.setProfileImageFile(file)
            }
            binding.pofilePicImageView.setImageURI(uri)
            viewModel.setSelectedProfileImage(uri)
            viewModel.pfpSuccess(firebaseAuth.currentUser?.uid.toString())
        }
    }


}