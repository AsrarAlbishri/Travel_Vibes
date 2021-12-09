package com.tuwaiq.travelvibes.postListFragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.tuwaiq.travelvibes.R

class PostListFragment : Fragment() {

    private lateinit var postRecyclerView: RecyclerView

    private lateinit var viewModel: PostListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.post_list_fragment, container, false)

        postRecyclerView = view.findViewById(R.id.post_recycler_View)

        return view
    }

    override fun onStart() {
        super.onStart()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



}