package com.tuwaiq.travelvibes.favorite

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.data.Comment
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.databinding.FragmentFavoriteBinding
import com.tuwaiq.travelvibes.databinding.ListItemFavPostBinding
import com.tuwaiq.travelvibes.postListFragment.PostListFragmentDirections
//import com.tuwaiq.travelvibes.profileFragment.FavoriteFragmentArgs
import kotlinx.coroutines.launch
import java.util.*


private const val TAG = "FavoriteFragment"
class FavoriteFragment : Fragment() {

    private val favoriteViewModel: FavoriteViewModel by lazy { ViewModelProvider(this)[FavoriteViewModel::class.java] }
    
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var post: Post

    private lateinit var user: User

//    private val args: FavoriteFragmentArgs by navArgs()

    lateinit var postId:String

    private val dateFormat = "EEE, MMM dd, yyyy"

    //private val favoriteCollectionRef = Firebase.firestore.collection("favorite post")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  postId = args.postId

       // user = User()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
       binding = FragmentFavoriteBinding.inflate(layoutInflater)
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(context)


        lifecycleScope.launch {

            favoriteViewModel.getFavoritePost(Firebase.auth.currentUser?.uid!!).observeForever {

                val favoritePosts : MutableList<Post> = mutableListOf()
                       it.favorite.forEach {
                           lifecycleScope.launch {
                               favoriteViewModel.detailsPost(it).observe(
                                   viewLifecycleOwner, {
                                        if(!favoritePosts.contains(it)) {
                                            favoritePosts += it
                                        }
                                       binding.favoriteRecyclerView.adapter = PostFavoriteAdapter(favoritePosts)

                                   }
                               )
                           }
                       }
            }


            }

        
        return binding.root
    }
    
    
    private inner class PostFavoriteHolder(val binding: ListItemFavPostBinding)
        :RecyclerView.ViewHolder(binding.root),View.OnClickListener{
            
            private lateinit var post: Post

            init {
                itemView.setOnClickListener(this)
            }
            
            fun bind(post: Post){
                this.post = post
                postId = post.postId
                
                binding.postFavPlace.text = post.placeName



//                if (!post.date.isNullOrEmpty()) {
//                    binding.dataFavPost.text = DateFormat.format(dateFormat, post.date.toLong())
//                }
                
                binding.favPostImage.load(post.postImageUrl)
                
            }

        override fun onClick(p0: View?) {

            if(p0 == itemView){
                val action = FavoriteFragmentDirections.actionNavigationFavToNavigationHome(post.postId)
                findNavController().navigate(action)

            }

        }
    }
    
    private inner class PostFavoriteAdapter(val posts:List<Post>)
        :RecyclerView.Adapter<PostFavoriteHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostFavoriteHolder {
             val binding = ListItemFavPostBinding.inflate(
                 layoutInflater,
                 parent,
                 false
             )
            
            return PostFavoriteHolder(binding)
        }

        override fun onBindViewHolder(holder: PostFavoriteHolder, position: Int) {
           val post = posts[position]
            holder.bind(post)
        }

        override fun getItemCount(): Int = posts.size
        
    }

}