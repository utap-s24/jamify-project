package com.example.jamify

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.jamify.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    //variables
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth


    private val viewModel: MainViewModel by activityViewModels()

    //posts
    private lateinit var adapter: RVDiffAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

    }

//    private fun initAdapter(binding: FragmentRvBinding) {
    private fun initAdapter(binding: FragmentProfileBinding) {
        val postRowAdapter = RVDiffAdapter(viewModel){}
        binding.postsRecyclerView.adapter = postRowAdapter
        // XXX Write me, observe posts
        viewModel.observeUserPosts().observe(viewLifecycleOwner) {
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
        viewModel.fetchInitialNotes(){}
        viewModel.observeUserPosts().observe(viewLifecycleOwner) { _ ->
            // Extract unique song titles from user's posts
            val songTitles = viewModel.extractSongTitles().toTypedArray()
            val filterSpinner = binding.filterSpinner
            val filterAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, songTitles)
            filterAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            filterSpinner.adapter = filterAdapter

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

        // Set the choices for the spinner
        val spinnerArray = ArrayList<String>()
        spinnerArray.add("one song")
        spinnerArray.add("two song")
        spinnerArray.add("three song")
        spinnerArray.add("four sifheowirfherwoughbnwer")
        spinnerArray.add("five")


//
        val filterSpinner = binding.filterSpinner
        val filterAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, songTitles)
        filterAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = filterAdapter



        binding.usernameTextView.text = currentUser?.displayName
//        binding.recyclerView.layoutManager = LinearLayoutManager(context)
//        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
//        initSwipeLayout(binding.swipeRefreshLayout)


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


}