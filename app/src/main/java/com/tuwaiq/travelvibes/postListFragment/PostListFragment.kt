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
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.firebase.firestore.ktx.firestore
import com.tuwaiq.travelvibes.commentFragment.CommentViewModel
import com.tuwaiq.travelvibes.data.Comment
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.postFragment.PostFragmentArgs
import com.tuwaiq.travelvibes.postFragment.PostFragmentDirections
import com.tuwaiq.travelvibes.postFragment.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

private const val TAG = "PostListFragment"

class PostListFragment : Fragment() {

    private val postListViewModel: PostListViewModel by lazy { ViewModelProvider(this)[PostListViewModel::class.java] }

    private val favoriteCollectionRef = Firebase.firestore.collection("favorite post")


    val postList = mutableListOf<Post>()

  //  private val args: PostListFragmentArgs by navArgs()

    val database = FirebaseFirestore.getInstance()

    private val dateFormat = "EEE, MMM dd, yyyy"


    private lateinit var binding:PostListFragmentBinding
    lateinit var postId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // postId = args.postId
    }

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
            binding.commentIV.setOnClickListener(this)
            binding.favoritIV.setOnClickListener(this)
        }

            fun bind(post:Post){
                this.post = post
                postId = post.postId
                binding.postDetails.text = post.postDescription
                Log.d(TAG, "bind: ${post.date}")
                
                if (!post.date.isNullOrEmpty()) {
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

            if (p0 == binding.commentIV){
                val action = PostListFragmentDirections.actionNavigationHomeToCommentFragment(post.postId)
                findNavController().navigate(action)

            }

            if (p0 == binding.favoritIV){

              //  binding.favoritIV.setBackgroundColor(getResources().getColor(R.color.red))

                lifecycleScope.launch(Dispatchers.IO){
                val originalList:MutableList<String> = (Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
                    .get()
                    .await()
                    .toObject(User::class.java)
                    ?.favorite ?: emptyList()) as MutableList<String>

                    originalList += post.postId

                    Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
                        .update("favorite" , originalList)


                }

                val action = PostListFragmentDirections.actionNavigationHomeToNavigationFav(post.postId)
                findNavController().navigate(action)

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