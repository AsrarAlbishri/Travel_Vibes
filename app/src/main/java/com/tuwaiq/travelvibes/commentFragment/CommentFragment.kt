package com.tuwaiq.travelvibes.commentFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.data.Comment
import com.tuwaiq.travelvibes.databinding.FragmentCommentBinding
import com.tuwaiq.travelvibes.databinding.ListItemCommentBinding
import com.tuwaiq.travelvibes.databinding.PostListFragmentBinding


class CommentFragment : Fragment() {

    private val commentViewModel:CommentViewModel by lazy { ViewModelProvider(this)[CommentViewModel::class.java] }

    private lateinit var comment: Comment

    private lateinit var firebaseUser: FirebaseUser


    private lateinit var binding : FragmentCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseUser = Firebase.auth.currentUser!!

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
            commentViewModel.addComment(comment)
        }

        return binding.root
    }

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