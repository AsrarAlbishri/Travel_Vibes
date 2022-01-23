package com.tuwaiq.travelvibes.favorite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.databinding.FragmentFavoriteBinding
import com.tuwaiq.travelvibes.databinding.ListItemFavPostBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private const val TAG = "FavoriteFragment"

class FavoriteFragment : Fragment() {

    private val favoriteViewModel: FavoriteViewModel by lazy { ViewModelProvider(this)[FavoriteViewModel::class.java] }

    private lateinit var mainBinding: FragmentFavoriteBinding

    lateinit var postId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainBinding = FragmentFavoriteBinding.inflate(layoutInflater)
        mainBinding.favoriteRecyclerView.layoutManager = GridLayoutManager(context, 2)


        lifecycleScope.launch {

            favoriteViewModel.getFavoritePost(Firebase.auth.currentUser?.uid!!).observeForever {

                val favoritePosts: MutableList<Post> = mutableListOf()
                it.favorite.forEach {
                    lifecycleScope.launch {
                        favoriteViewModel.detailsPost(it).observe(
                            viewLifecycleOwner, {
                                if (!favoritePosts.contains(it)) {
                                    favoritePosts += it
                                }
                                mainBinding.favoriteRecyclerView.adapter =
                                    PostFavoriteAdapter(favoritePosts)

                            }
                        )
                    }
                }
            }

        }


        return mainBinding.root
    }


    private inner class PostFavoriteHolder(val binding: ListItemFavPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var post: Post


        init {

            binding.deleteFav.setOnClickListener {

                lifecycleScope.launch {
                    val originalList: MutableList<String> =
                        (Firebase.firestore.collection("users")
                            .document(Firebase.auth.currentUser?.uid!!)
                            .get()
                            .await()
                            .toObject(User::class.java)
                            ?.favorite ?: emptyList()) as MutableList<String>

                    originalList.remove(post.postId)
                    //  originalList.removeAt(adapterPosition)

                    Firebase.firestore.collection("users")
                        .document(Firebase.auth.currentUser?.uid!!)
                        .update("favorite", originalList.distinct())
                    if (originalList.isNotEmpty()) {
                        favoriteViewModel.getFavoritePost(Firebase.auth.currentUser?.uid!!)
                            .observe(viewLifecycleOwner) {
//
                                val favoritePosts: MutableList<Post> = mutableListOf()
                                it.favorite.distinct().forEach {


                                    lifecycleScope.launch {
                                        favoriteViewModel.detailsPost(it).observe(
                                            viewLifecycleOwner, {
                                                Log.d(TAG, "favo: $it")
                                                favoritePosts += it
                                                mainBinding.favoriteRecyclerView.adapter =
                                                    PostFavoriteAdapter(favoritePosts)

                                            }
                                        )
                                    }
                                }
                            }
                    } else {
                        mainBinding.favoriteRecyclerView.adapter = PostFavoriteAdapter(emptyList())
                    }
                }

            }

        }

        fun bind(post: Post) {
            this.post = post
            postId = post.postId

            binding.postFavPlace.text = post.placeName


            binding.favPostImage.load(post.postImageUrl)


        }
    }


    private inner class PostFavoriteAdapter(val posts: List<Post>) :
        RecyclerView.Adapter<PostFavoriteHolder>() {
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