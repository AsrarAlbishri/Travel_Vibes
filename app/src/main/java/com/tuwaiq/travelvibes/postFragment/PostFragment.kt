package com.tuwaiq.travelvibes.postFragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.databinding.FragmentProfileEditBinding
import com.tuwaiq.travelvibes.databinding.PostFragmentBinding
import com.tuwaiq.travelvibes.profileFragment.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

private const val TAG = "PostFragment"
class PostFragment : Fragment() {


    private val postViewModel: PostViewModel by lazy { ViewModelProvider(this)[PostViewModel::class.java] }

    private lateinit var binding: PostFragmentBinding

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        post = Post()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding= PostFragmentBinding.inflate(layoutInflater)

        binding.addPost.setOnClickListener {
            binding.apply {
                post.postDescription=postWrite.text.toString()
                post.placeName=placeName.text.toString()
            }

            postViewModel.savePost(post)
        }

        return binding.root
    }



}