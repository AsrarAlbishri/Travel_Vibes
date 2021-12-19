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

private const val TAG = "PostListFragment"

class PostListFragment : Fragment() {

    val postList = mutableListOf<Post>()

    val database = FirebaseFirestore.getInstance()

    private val dateFormat = "EEE, MMM dd, yyyy"



    private val postListViewModel: PostListViewModel by lazy { ViewModelProvider(this)[PostListViewModel::class.java] }

    private lateinit var binding:PostListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PostListFragmentBinding.inflate(layoutInflater)
        binding.postRecyclerView.layoutManager=LinearLayoutManager(context)


       fetchData()

        return binding.root

    }

    private fun fetchData(){
        database.collection("posts")
            .get()
            .addOnSuccessListener {

                for (document in it){
                    val post = document.toObject(Post::class.java)
                    postList.add(post)

                }

                binding.postRecyclerView.adapter =PostsAdapter(postList)
            }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private inner class PostsHolder(val binding: ListItemPostBinding)
        :RecyclerView.ViewHolder(binding.root),View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
        }

            fun bind(post:Post){

                binding.postDetails.text = post.postDescription
                binding.postDateItem.text =DateFormat.format(dateFormat, post.date)

            }

        override fun onClick(p0: View?) {

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