package com.example.jamify

import android.media.MediaPlayer
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jamify.com.example.jamify.DataAdapter
import com.example.jamify.databinding.FragmentCreateBinding
import com.example.jamify.databinding.FragmentHomeBinding
import androidx.core.net.toUri

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    lateinit var recyclerView: RecyclerView
    lateinit var myAdapter: DataAdapter

    // XXX initialize viewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    //trying to create one media player in each frag
//    var mediaPlayer: MediaPlayer = MediaPlayer()
    private var lastPlayedIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }
    fun progressBarOn() {
        binding.indeterminateBar.visibility = View.VISIBLE
    }

    fun progressBarOff() {
        binding.indeterminateBar.visibility = View.GONE
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        val postRowAdapter = PostRowAdapter(requireActivity(), viewModel)

        binding.recyclerView.adapter = postRowAdapter

        Log.d(javaClass.simpleName, "notifyDataSetChanged")

        binding.indeterminateBar.visibility = View.VISIBLE
        viewModel.fetchInitialPosts {
            binding.indeterminateBar.visibility = View.GONE
        }

        // XXX Write me, observe posts
        viewModel.observePublicPosts().observe(viewLifecycleOwner) {
           postRowAdapter.submitList(it)
            postRowAdapter.notifyDataSetChanged()
        }
    }



    override fun onStop() {
        super.onStop()
        viewModel.mediaPlayer.stop()
//        recyclerView.adapter?.notifyDataSetChanged()
//        viewModel.mediaPlayer = null
        viewModel.setSongPlayingPos(-1)

    }
}