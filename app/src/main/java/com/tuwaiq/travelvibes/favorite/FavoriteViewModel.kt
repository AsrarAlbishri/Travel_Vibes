package com.tuwaiq.travelvibes.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User

class FavoriteViewModel:ViewModel() {

    private val repo: AppRepository = AppRepository.getInstance()

    suspend fun getFavoritePost(uid: String ): LiveData<User>{
       return repo.getUserInfo(uid)
}

    suspend fun detailsPost(uid:String): LiveData<Post> {
        return repo.detailsPost(uid)

    }

}