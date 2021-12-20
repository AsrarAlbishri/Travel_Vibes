package com.tuwaiq.travelvibes.postFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Post
import java.io.File

class PostViewModel : ViewModel() {

    private val repo: AppRepository = AppRepository.getInstance()

    fun savePost(post: Post){
        repo.addPost(post)
    }

    fun getPhotoFile(post: Post): File = repo.getPhotoFile(post)

    suspend fun updatePost(uid:String): LiveData<DocumentSnapshot> {
        return repo.updatePost(uid)

    }
}