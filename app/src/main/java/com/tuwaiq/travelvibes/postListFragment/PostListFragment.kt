package com.tuwaiq.travelvibes.postListFragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import android.view.*
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.firebase.auth.FirebaseAuth
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
import kotlin.collections.ArrayList

private const val TAG = "PostListFragment"

class PostListFragment : Fragment() {

    private val postListViewModel: PostListViewModel by lazy { ViewModelProvider(this)[PostListViewModel::class.java] }




    val postList = mutableListOf<Post>()

//    private val args: PostListFragmentArgs by navArgs()

    val database = FirebaseFirestore.getInstance()

    private val dateFormat = "EEE, MMM dd, yyyy"


    private lateinit var binding:PostListFragmentBinding
    lateinit var postId:String

   private lateinit var auth: FirebaseAuth


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu,menu)


        val searchItem = menu.findItem(R.id.search_action)
        if (searchItem != null){
            val searchView = searchItem.actionView as SearchView
            searchView.queryHint = "Search for..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {

                    return false

                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isNotEmpty()){

                        val search = newText.lowercase(Locale.getDefault())

                        lifecycleScope.launch {

                            postListViewModel.getSearchPosts(search).observeForever { postList ->

                                lifecycleScope.launch {
                                    postListViewModel.getUserInfo(Firebase.auth.currentUser?.uid!!).observe(viewLifecycleOwner){ user ->
                                        binding.postRecyclerView.adapter = PostsAdapter(postList,user)
                                    }
                                }

                            }


                        }

                    }

                    return true

                }

            })
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

       auth = FirebaseAuth.getInstance()

       // postId = args.postId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PostListFragmentBinding.inflate(layoutInflater)
        binding.postRecyclerView.layoutManager=LinearLayoutManager(context)


        lifecycleScope.launch {

            postListViewModel.getFetchPosts().observeForever(  Observer { postList->

                lifecycleScope.launch {
                    postListViewModel.getUserInfo(Firebase.auth.currentUser?.uid!!).observe(viewLifecycleOwner){user->
                        Log.d(TAG, "onCreateView: postList $postList")
                        updateUI(postList, user)
                    }
                }

            })

        }

        return binding.root

    }

    private fun updateUI(postList: List<Post>, user: User){
        binding.postRecyclerView.adapter = PostsAdapter(postList , user)

    }

    private inner class PostsHolder(val binding: ListItemPostBinding)
        :RecyclerView.ViewHolder(binding.root),View.OnClickListener{


       // var favBtnClicked = false

       lateinit var post: Post
       lateinit var user: User
       var postion = 0

        lateinit var posts: MutableList<Post>

        init {
            itemView.setOnClickListener(this)
            binding.deletPostIV.setOnClickListener(this)
            binding.commentIV.setOnClickListener(this)
            //binding.favoritIV.setOnClickListener(this)
        }


            fun bind(post:Post,user: User, postion: Int,posts: MutableList<Post>){
                this.post = post
                this.user = user
                this.postion = postion
                this.posts = posts
                postId = post.postId
                binding.postDetails.text = post.postDescription
                Log.d(TAG, "bind: ${post.ownerId}")
                
                if (!post.date.isNullOrEmpty()) {
                    binding.postDateItem.text = DateFormat.format(dateFormat, post.date.toLong())
                }

                binding.imageViewOfPost.load(post.postImageUrl)

               binding.favoritIV.isChecked = user.favorite.contains(postId)

                binding.locationAddressText.text = post.location

//                if (post.location.contains(post.location)){
//                    binding.locationIV.visibility = View.VISIBLE
//                }else{
//                    binding.locationIV.visibility = View.GONE
//                }



                        lifecycleScope.launch(Dispatchers.Main) {
                            Firebase.firestore.collection("users").document(post.ownerId)

                                .get().addOnSuccessListener {

                                    val postUser = it.toObject(User::class.java)
                                    Log.d(TAG, "bind: $postUser  owner ${post.ownerId}")
                                    binding.userPost.text = postUser?.userName
                                    binding.profilePostIV.load(postUser?.profileImageUrl)
                                }.addOnFailureListener {
                                    Log.e(TAG, "bind: ",it)
                                }
                        }


                binding.favoritIV.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked){
                       addToFav()
                        Log.d(TAG, "bind: $isChecked")

                    }else{
                        Log.d(TAG, "bind: $isChecked")
                        removeFromFav(post)
                    }

                }

                binding.userPost.setOnClickListener {

                        val action = PostListFragmentDirections.actionNavigationHomeToNavigationProfile(post.ownerId)
                       findNavController().navigate(action)
                }
            }

        override fun onClick(p0: View?) {

            if(p0 == itemView){

                    val action = PostListFragmentDirections.actionPostListFragmentToPostFragment(post.postId)
                     findNavController().navigate(action)
            }

            if (p0 == binding.deletPostIV){

                if (auth.currentUser!!.uid == post.ownerId){
                    postListViewModel.deletePost(post)
                    posts.removeAt(adapterPosition)
                    updateUI(posts, user)


                }

//                    Firebase.firestore.collection("posts").document(post.postId)
//                        .update("postId",post)

            }

            if (p0 == binding.commentIV){
                val action = PostListFragmentDirections.actionNavigationHomeToCommentFragment(post.postId)
                findNavController().navigate(action)
              //  findNavController().popBackStack()

            }
        }
    }

    private fun removeFromFav(post: Post) {
        lifecycleScope.launch(Dispatchers.IO) {
            val originalList: MutableList<String> =
                (Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
                    .get()
                    .await()
                    .toObject(User::class.java)
                    ?.favorite ?: emptyList()) as MutableList<String>

           // originalList -= post.postId
            originalList.remove(post.postId)

            Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
                .update("favorite", originalList)


        }
    }

    private fun PostsHolder.addToFav() {
        lifecycleScope.launch(Dispatchers.IO) {
            val originalList: MutableList<String> =
                (Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
                    .get()
                    .await()
                    .toObject(User::class.java)
                    ?.favorite ?: emptyList()) as MutableList<String>

            originalList += post.postId

            Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
                .update("favorite", originalList)


        }
    }

    private inner class PostsAdapter(val posts:List<Post>,val user: User):RecyclerView.Adapter<PostsHolder>(){
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
            holder.bind(post,user,position, posts as MutableList<Post>)

        }

        override fun getItemCount(): Int = posts.size

    }
}