package com.tuwaiq.travelvibes.commentFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.data.Comment
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.databinding.FragmentCommentBinding
import com.tuwaiq.travelvibes.databinding.ListItemCommentBinding
import com.tuwaiq.travelvibes.databinding.PostListFragmentBinding
import com.tuwaiq.travelvibes.postFragment.PostFragmentArgs
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private const val TAG = "CommentFragment"
class CommentFragment : Fragment() {

    private val commentViewModel:CommentViewModel by lazy { ViewModelProvider(this)[CommentViewModel::class.java] }

    private lateinit var comment: Comment

    private lateinit var firebaseUser: FirebaseUser

    private val args: CommentFragmentArgs by navArgs()

    lateinit var postId:String

    val database = FirebaseFirestore.getInstance()


    private lateinit var binding : FragmentCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseUser = Firebase.auth.currentUser!!
        postId = args.postId

        comment = Comment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommentBinding.inflate(layoutInflater)
        binding.commentRV.layoutManager= LinearLayoutManager(context)

        binding.addComment.setOnClickListener {
            binding.apply {
                comment.commentDetails = enterComment.text.toString()
            }

            comment.userId = firebaseUser.uid
           // comment.userName = firebaseUser.displayName
            commentViewModel.addComment(comment,postId)
        }

        lifecycleScope.launch {

            commentViewModel.getComments(postId).observeForever {

//                it.forEach {
//                    val comments = it.comment
//
//                    binding.commentRV.adapter = CommentAdapter(comments)
//                }
                Log.d(TAG, "onCreateView: ${it}")
                binding.commentRV.adapter = CommentAdapter(it)

            }
        }

        return binding.root
    }

//    fun userComment(){
//        var user=User(Firebase.auth.uid!!)
//        val uid = FirebaseAuth.getInstance().currentUser!!.uid
//        val userRef = database.collection("users")
//        val uidRef = userRef.document(uid)
//        uidRef.get().addOnSuccessListener { document ->
//            if (document != null){
//                user = document.toObject(User::class.java)!!
//
//            }
//
//        }
//    }


    private inner class CommentHolder(val binding:ListItemCommentBinding)
        :RecyclerView.ViewHolder(binding.root){

        private lateinit var comment: Comment


            fun bind(comment: Comment){
                this.comment = comment
                binding.commentUserName.text = comment.userName
                binding.commentText.text = comment.commentDetails

            }
        }

    private inner class CommentAdapter(val comments:List<Comment>):RecyclerView.Adapter<CommentHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
             val binding = ListItemCommentBinding.inflate(
                 layoutInflater,
                 parent,
                 false

             )

            return CommentHolder(binding)
        }

        override fun onBindViewHolder(holder: CommentHolder, position: Int) {
           val comment = comments[position]
            holder.bind(comment)
        }

        override fun getItemCount(): Int = comments.size

    }


}