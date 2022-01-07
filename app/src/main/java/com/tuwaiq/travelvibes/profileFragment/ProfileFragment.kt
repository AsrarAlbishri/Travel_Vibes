package com.tuwaiq.travelvibes.profileFragment

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.tuwaiq.travelvibes.authentication.LoginFragmentDirections
import com.tuwaiq.travelvibes.authentication.RegisterFragment.Companion.auth
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.databinding.ListItemPostBinding
import com.tuwaiq.travelvibes.databinding.PostListProfileFragmentBinding
import com.tuwaiq.travelvibes.databinding.ProfileFragmentBinding
import com.tuwaiq.travelvibes.postListFragment.PostListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

private const val TAG = "ProfileFragment"
class ProfileFragment : Fragment() {

    private val  profileViewModel: ProfileViewModel by lazy { ViewModelProvider(this)[ProfileViewModel::class.java] }

   private lateinit var binding: ProfileFragmentBinding

    private val dateFormat = "EEE, MMM dd, yyyy"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = auth.currentUser

        if (currentUser == null){
            val navCon = findNavController()
            val action = ProfileFragmentDirections.actionNavigationProfileToLoginFragment()
            navCon.navigate(action)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ProfileFragmentBinding.inflate(layoutInflater)
        binding.postProfileRv.layoutManager = LinearLayoutManager(context)


        lifecycleScope.launch {
            profileViewModel.profilePostData().observe(viewLifecycleOwner ,{ postList ->
                binding.postProfileRv.adapter = PostProfileAdapter(postList)
            })
        }

        lifecycleScope.launch {
            profileViewModel.getUserInfo().observe(viewLifecycleOwner,{

                binding.name.setText(it.firstName)
               binding.userName.setText(it.userName)
                binding.usrBio.setText(it.bio)
               binding.photoProfile.load(it.profileImageUrl)
               Log.d( TAG, "hhhhhhhh h${it.profileImageUrl} vvv $it")
            })
        }


        return binding.root
    }


    private inner class PostsProfileHolder(val binding:PostListProfileFragmentBinding)
        : RecyclerView.ViewHolder(binding.root){

        private lateinit var post: Post

        fun bind(post:Post){

            this.post = post

            binding.postProfileDetails.text = post.postDescription
            if (!post.date.isNullOrEmpty()) {
                binding.dataUserPost.text = DateFormat.format(dateFormat, post.date.toLong())
            }

            binding.profilePostImage.load(post.postImageUrl)


        }

    }

    private inner class PostProfileAdapter(val posts:List<Post>):RecyclerView.Adapter<PostsProfileHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsProfileHolder {
             val binding = PostListProfileFragmentBinding.inflate(
                 layoutInflater,
                 parent,
                 false
             )

            return PostsProfileHolder(binding)
        }

        override fun onBindViewHolder(holder: PostsProfileHolder, position: Int) {
            val post = posts[position]
            holder.bind(post)

        }

        override fun getItemCount(): Int = posts.size

    }


    override fun onStart() {
        super.onStart()

        binding.editProfileBtn.setOnClickListener {
            val navCon = findNavController()
            val action = ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment()
            navCon.navigate(action)
        }

    }

}