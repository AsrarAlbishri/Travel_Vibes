package com.tuwaiq.travelvibes.postListFragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.databinding.ListItemPostBinding
import com.tuwaiq.travelvibes.databinding.PostListFragmentBinding
import android.text.format.DateFormat
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import coil.load
import com.tuwaiq.travelvibes.postFragment.PostFragmentDirections
import com.tuwaiq.travelvibes.postFragment.PostViewModel
import kotlinx.coroutines.launch

private const val TAG = "PostListFragment"

class PostListFragment : Fragment() {

    private val postListViewModel: PostListViewModel by lazy { ViewModelProvider(this)[PostListViewModel::class.java] }

    val postList = mutableListOf<Post>()

    val database = FirebaseFirestore.getInstance()

    private val dateFormat = "EEE, MMM dd, yyyy"


    private lateinit var binding:PostListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PostListFragmentBinding.inflate(layoutInflater)
        binding.postRecyclerView.layoutManager=LinearLayoutManager(context)


        lifecycleScope.launch {



            postListViewModel.getFetchPosts().observeForever(  Observer {
                binding.postRecyclerView.adapter = PostsAdapter(it)
               // binding.postRecyclerView.adapter!!.notifyDataSetChanged()
            })

        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private inner class PostsHolder(val binding: ListItemPostBinding)
        :RecyclerView.ViewHolder(binding.root),View.OnClickListener{

       private lateinit var post: Post
        init {
            itemView.setOnClickListener(this)
            binding.deletPostIV.setOnClickListener(this)
        }

            fun bind(post:Post){
                this.post = post
                binding.postDetails.text = post.postDescription
                if (post.date.isNotEmpty()) {
                    binding.postDateItem.text = DateFormat.format(dateFormat, post.date.toLong())
                }
                binding.imageViewOfPost.load(post.postImageUrl)


            }

        override fun onClick(p0: View?) {

            if(p0 == itemView){

                    val action = PostListFragmentDirections.actionPostListFragmentToPostFragment(post.postId)
                     findNavController().navigate(action)
            }

            if (p0 == binding.deletPostIV){

                    postListViewModel.deletePost(post)



            }

        }
    }

    private inner class PostsAdapter(val posts:List<Post>):RecyclerView.Adapter<PostsHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsHolder {
            val binding = ListItemPostBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            return PostsHolder(binding)
        }

        override fun onBindViewHolder(holder: PostsHolder, position: Int) {
            val post = posts[position]
            holder.bind(post)

        }

        override fun getItemCount(): Int = posts.size

    }
}