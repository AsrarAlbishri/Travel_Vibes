package com.tuwaiq.travelvibes.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.CollectionReference
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User

class FavoriteViewModel:ViewModel() {

    private val repo: AppRepository = AppRepository.getInstance()

    suspend fun getFavoritePost(uid: String): LiveData<List<Post>>{
        return repo.getFavoritePost(uid)
    }






}