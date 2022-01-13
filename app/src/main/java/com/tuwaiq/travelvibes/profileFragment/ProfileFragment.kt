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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.authentication.RegisterFragment.Companion.auth
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.databinding.PostListProfileFragmentBinding
import com.tuwaiq.travelvibes.databinding.ProfileFragmentBinding
import kotlinx.coroutines.launch

private const val TAG = "ProfileFragment"
class ProfileFragment : Fragment() {

    private val  profileViewModel: ProfileViewModel by lazy { ViewModelProvider(this)[ProfileViewModel::class.java] }

   private lateinit var binding: ProfileFragmentBinding

   private val args:ProfileFragmentArgs by navArgs()

    lateinit var ownerId:String

    private val currentUser = auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ownerId = args.ownerId


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
        binding.postProfileRv.layoutManager = GridLayoutManager(context,2)
        binding.postProfileRv.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        binding.postProfileRv.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.HORIZONTAL))

        if (args.ownerId == "-1"){
            lifecycleScope.launch {
                profileViewModel.profilePostData(Firebase.auth.currentUser?.uid!!).observe(viewLifecycleOwner,{ postList ->
                    binding.postProfileRv.adapter = PostProfileAdapter(postList)

                })
            }

        }else{
            lifecycleScope.launch {
                profileViewModel.profilePostData(args.ownerId).observe(viewLifecycleOwner,{ postList ->
                    binding.postProfileRv.adapter = PostProfileAdapter(postList)

                })
            }

        }


       if (args.ownerId == "-1"){
           showUserInfo(Firebase.auth.currentUser?.uid!!)
           binding.editProfileBtn.visibility = View.VISIBLE
       }else{
           showUserInfo(args.ownerId)

       }

        return binding.root
    }

    private fun showUserInfo(uid:String) {




        lifecycleScope.launch {

                profileViewModel.getUserInfo(uid).observe(viewLifecycleOwner, {

                    binding.name.setText(it.firstName)
                    binding.userName.setText(it.userName)
                    binding.usrBio.setText(it.bio)
                    binding.photoProfile.load(it.profileImageUrl)
                    Log.d(TAG, "hhhhhhhh h${it.profileImageUrl} vvv $it")

                })
        }
    }


    private inner class PostsProfileHolder(val binding:PostListProfileFragmentBinding)
        : RecyclerView.ViewHolder(binding.root),View.OnClickListener{

        private lateinit var post: Post

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(post:Post){

            this.post = post


            binding.profilePostImage.load(post.postImageUrl)

        }

        override fun onClick(v: View?) {
            if (v == itemView){
                val action = ProfileFragmentDirections.actionNavigationProfileToDetailsFragment(post.postId)
                findNavController().navigate(action)

            }

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



        binding.settingIV.setOnClickListener {
            val navCon = findNavController()
            val action = ProfileFragmentDirections.actionNavigationProfileToSettingFragment()
            navCon.navigate(action)
        }

    }

}