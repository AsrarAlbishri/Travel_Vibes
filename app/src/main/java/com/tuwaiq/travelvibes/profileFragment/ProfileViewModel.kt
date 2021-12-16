package com.tuwaiq.travelvibes.profileFragment

import androidx.lifecycle.ViewModel
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User

class ProfileViewModel : ViewModel() {

    private val repo:AppRepository = AppRepository.getInstance()

    fun saveUser(user:User){
        repo.saveUserInfo(user)
    }


}