package com.tuwaiq.travelvibes.postFragment

import androidx.lifecycle.ViewModel
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Post

class PostViewModel : ViewModel() {

    private val repo: AppRepository = AppRepository()

    fun savePost(post: Post){
        repo.addPost(post)
    }
}