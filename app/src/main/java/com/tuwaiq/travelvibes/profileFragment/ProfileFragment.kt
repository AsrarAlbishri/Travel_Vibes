package com.tuwaiq.travelvibes.profileFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.tuwaiq.travelvibes.authentication.LoginFragmentDirections
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

   private lateinit var binding: ProfileFragmentBinding



   private lateinit var user: User



    val postList = mutableListOf<Post>()

    // هوا بدل auth
    val database = FirebaseFirestore.getInstance()

    private val personCollectionRef = Firebase.firestore.collection("users")





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ProfileFragmentBinding.inflate(layoutInflater)
        binding.postProfileRv.layoutManager = LinearLayoutManager(context)


        profilePostData()

        getUserData()

        return binding.root
    }


    private fun getUserData(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = database.collection("users")
        val uidRef = userRef.document(uid)
        uidRef.get().addOnSuccessListener { document ->
            if (document != null){
                binding.name.setText(document.getString("email"))
                binding.userName.setText(document.getString("userName"))

            }else{
                Log.d(TAG , "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with" , exception)

        }
    }

    private fun profilePostData(){
        Firebase.auth.currentUser?.let {
            database.collection("posts").whereArrayContains("id", it.uid)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        val post = document.toObject(Post::class.java)
                        postList.add(post)

                    }
                    binding.postProfileRv.adapter = PostProfileAdapter(postList)

                }
        }
    }

    private inner class PostsProfileHolder(val binding:PostListProfileFragmentBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(post:Post){

            binding.postProfileDetails.text = post.postDescription


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



    private fun savePerson(person: User) = CoroutineScope(Dispatchers.IO).launch {
        try {

            personCollectionRef.add(person).await()

        }catch (e:Exception){
            withContext(Dispatchers.Main){
                //Toast.makeText(this, e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.settingIV.setOnClickListener {
            val navCon = findNavController()
            val action = ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment()
            navCon.navigate(action)
        }

    }




}