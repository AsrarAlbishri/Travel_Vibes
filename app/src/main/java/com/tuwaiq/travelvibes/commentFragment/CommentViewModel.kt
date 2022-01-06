package com.tuwaiq.travelvibes.commentFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Comment
import com.tuwaiq.travelvibes.data.CommentUser
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User

class CommentViewModel : ViewModel(){

    private val repo: AppRepository = AppRepository.getInstance()

    fun addComment(comment: Comment,postId: String){
        repo.addComment(comment,postId)
    }

    suspend fun getComments(postId: String): LiveData<List<CommentUser>> {
        return repo.getComments(postId)
    }

//    suspend fun getComments(postId: String): LiveData<List<Comment>> {
//       return repo.getComments(postId)
//    }

//    suspend fun getUserCommentInfo(postId: String): LiveData<User>{
//        return repo.getUserCommentInfo(postId)
//    }


}