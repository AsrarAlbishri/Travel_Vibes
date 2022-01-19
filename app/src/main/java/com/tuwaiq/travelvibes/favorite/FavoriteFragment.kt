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
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.data.Comment
import com.tuwaiq.travelvibes.data.CommentUser
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.databinding.FragmentFavoriteBinding
import com.tuwaiq.travelvibes.databinding.ListItemFavPostBinding
import com.tuwaiq.travelvibes.postListFragment.PostListFragmentDirections
import kotlinx.coroutines.Dispatchers
//import com.tuwaiq.travelvibes.profileFragment.FavoriteFragmentArgs
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*


private const val TAG = "FavoriteFragment"
class FavoriteFragment : Fragment() {

    private val favoriteViewModel: FavoriteViewModel by lazy { ViewModelProvider(this)[FavoriteViewModel::class.java] }
    
    private lateinit var binding: FragmentFavoriteBinding

    lateinit var postId:String

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
        binding.favoriteRecyclerView.layoutManager = GridLayoutManager(context,2)


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
        :RecyclerView.ViewHolder(binding.root){
            
            private lateinit var post: Post


            
            fun bind(post: Post){
                this.post = post
                postId = post.postId
                
                binding.postFavPlace.text = post.placeName

                
                binding.favPostImage.load(post.postImageUrl)

                binding.deleteFav.setOnClickListener {

                    lifecycleScope.launch(Dispatchers.IO) {
                        val originalList: MutableList<String> =
                            (Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
                                .get()
                                .await()
                                .toObject(User::class.java)
                                ?.favorite ?: emptyList()) as MutableList<String>

                        originalList.remove(postId)

                        Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
                            .update("favorite", originalList)


                    }


                    lifecycleScope.launch {

                        favoriteViewModel.getFavoritePost(Firebase.auth.currentUser?.uid!!).observeForever {

                            val favoritePosts : MutableList<Post> = mutableListOf()
                            it.favorite.forEach {

                                updateUi(favoritePosts)
                                lifecycleScope.launch {
                                    favoriteViewModel.detailsPost(it).observe(
                                        viewLifecycleOwner, {
                                            favoritePosts.add(it)
                                        }
                                    )
                                }
                            }

                            updateUi(favoritePosts)
                        }


                    }



                }

            }

    }

    private fun updateUi(posts: List<Post>){
        val adapter = PostFavoriteAdapter(posts)

        binding.favoriteRecyclerView.adapter = adapter
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