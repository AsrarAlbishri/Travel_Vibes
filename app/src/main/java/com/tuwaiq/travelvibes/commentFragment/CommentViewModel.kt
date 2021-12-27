package com.tuwaiq.travelvibes.commentFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Comment
import com.tuwaiq.travelvibes.data.Post

class CommentViewModel : ViewModel(){

    private val repo: AppRepository = AppRepository.getInstance()

    fun addComment(comment: Comment,postId: String){
        repo.addComment(comment,postId)
    }

    suspend fun getComments(postId: String): LiveData<List<Comment>> {
       return repo.getComments(postId)
    }
}