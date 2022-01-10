package com.tuwaiq.travelvibes.detailsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.commentFragment.CommentViewModel
import com.tuwaiq.travelvibes.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment() {

    private val detailsViewModel: DetailsViewModel by lazy { ViewModelProvider(this)[DetailsViewModel::class.java] }

    private lateinit var binding: FragmentDetailsBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailsBinding.inflate(layoutInflater)

        return binding.root

    }


}