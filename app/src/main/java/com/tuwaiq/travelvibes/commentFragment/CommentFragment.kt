package com.tuwaiq.travelvibes.commentFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.data.Comment
import com.tuwaiq.travelvibes.data.CommentUser
import com.tuwaiq.travelvibes.databinding.FragmentCommentBinding
import com.tuwaiq.travelvibes.databinding.ListItemCommentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



private const val TAG = "CommentFragment"
class CommentFragment : Fragment() {

    private val commentViewModel:CommentViewModel by lazy { ViewModelProvider(this)[CommentViewModel::class.java] }

    private lateinit var comment: Comment
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private val args: CommentFragmentArgs by navArgs()
    lateinit var postId:String
    lateinit var commentUserList: List<CommentUser>


    private lateinit var binding : FragmentCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseUser = Firebase.auth.currentUser!!
        postId = args.postId
        auth = FirebaseAuth.getInstance()

        comment = Comment()

        commentUserList = listOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommentBinding.inflate(layoutInflater)
        binding.commentRV.layoutManager= LinearLayoutManager(context)

        binding.commentRV.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))

        lifecycleScope.launch {

            commentViewModel.getComments(postId).observe(viewLifecycleOwner,  {


                Log.d(TAG, "onCreateView: ${it}")
                updateUi(it)

            })
        }

        binding.addComment.setOnClickListener {
            binding.apply {
                comment.commentDetails = enterComment.text.toString()
            }

            comment.userId = firebaseUser.uid
            commentViewModel.addComment(comment,postId)

            lifecycleScope.launch {

                delay(300L)
                commentViewModel.getComments(postId).observe(viewLifecycleOwner,  {


                    Log.d(TAG, "onCreateView: ${it}")
                    updateUi(it)

                })
            }

            binding.enterComment.text.clear()

        }

//        lifecycleScope.launch {
//
//            commentViewModel.getComments(postId).observe(viewLifecycleOwner,  {
//
//
//                Log.d(TAG, "onCreateView: ${it}")
//                binding.commentRV.adapter = CommentAdapter(it ?: emptyList())
//
//            })
//        }

        return binding.root
    }

    private fun updateUi(comments: List<CommentUser>){
        val adapter = CommentAdapter(comments)

        binding.commentRV.adapter = adapter
    }


    private inner class CommentHolder(val binding:ListItemCommentBinding)
        :RecyclerView.ViewHolder(binding.root){

        private var comment: Comment? = null


            fun bind(comment: CommentUser){
                this.comment = comment.comment

                binding.commentText.text = comment.comment?.commentDetails
                binding.userIVComment.load(comment.user?.profileImageUrl)
                binding.commentUserName.text = comment.user?.userName

            }
        }

    private inner class CommentAdapter(val comments:List<CommentUser>):RecyclerView.Adapter<CommentHolder>(){
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