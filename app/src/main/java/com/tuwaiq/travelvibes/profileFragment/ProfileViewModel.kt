package com.tuwaiq.travelvibes.profileFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User

class ProfileViewModel : ViewModel() {

    private val repo:AppRepository = AppRepository.getInstance()

    fun saveUser(user:User){
        repo.saveUserInfo(user)
    }

    suspend fun profilePostData(uid: String): LiveData<List<Post>>{
       return repo.profilePostData(uid)
    }

    suspend fun getUserInfo(uid:String): LiveData<User>{
        return repo.getUserInfo(uid)
    }


}