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



   //private lateinit var user: User



    val database = FirebaseFirestore.getInstance()

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


       // profilePostData()

        getUserData()

        return binding.root
    }


    private fun getUserData(){
        var user=User(Firebase.auth.uid!!)
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = database.collection("users")
        val uidRef = userRef.document(uid)
        uidRef.get().addOnSuccessListener { document ->
            if (document != null){
                user = document.toObject(User::class.java)!!
                binding.name.setText(document.getString("firstName"))
                binding.userName.setText(document.getString("userName"))
                binding.usrBio.setText(document.getString("bio"))
                binding.photoProfile.load(user.profileImageUrl)
                Log.d(TAG, "hhhhhhhh h${user.profileImageUrl} vvv $user")

            }else{
                Log.d(TAG , "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with" , exception)

        }
    }

//    private fun profilePostData(){
//        Firebase.auth.currentUser?.let {
//            Log.d(TAG,it.uid)
//            database.collection("posts").whereEqualTo("ownerId",it.uid)
//                .get()
//                .addOnFailureListener {
//                    Log.e(TAG,"!!!!",it)
//                }
//                .addOnSuccessListener {
//                    for (document in it){
//
//                        val post = document.toObject(Post::class.java)
//                        Log.d(TAG,"khguy $document" )
//                        postList.add(post)
//
//                    }
//                    binding.postProfileRv.adapter = PostProfileAdapter(postList)
//
//                }
//        }
//    }

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