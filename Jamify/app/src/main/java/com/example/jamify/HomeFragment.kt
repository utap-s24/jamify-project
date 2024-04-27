package com.example.jamify

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
//        {
//            Log.d("OnePost",
//                String.format("OnePost title %s",
//                    if (it.title.length > 32)
//                        it.title.substring(0, 31) + "..."
//                    else it.title))
//            Log.d("doOnePost", "image ${it.imageURL}")
//            // XXX Write me
//            val action = HomeFragmentDirections.actionHomeFragmentToOnePostFragment(it)
//            findNavController().navigate(action)
//
//
//        }
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerView.adapter = postRowAdapter

        Log.d(javaClass.simpleName, "notifyDataSetChanged")

        binding.indeterminateBar.visibility = View.VISIBLE
        viewModel.fetchInitialPosts {
            binding.indeterminateBar.visibility = View.GONE
        }

        // XXX Write me, observe posts
        viewModel.observePublicPosts().observe(viewLifecycleOwner) {
           postRowAdapter.submitList(it)
        }



    }
}