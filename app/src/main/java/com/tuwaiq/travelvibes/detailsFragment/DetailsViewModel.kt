package com.tuwaiq.travelvibes.detailsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Post

class DetailsViewModel : ViewModel() {

    private val repo: AppRepository = AppRepository.getInstance()

    suspend fun detailsPost(uid: String): LiveData<Post> {
        return repo.detailsPost(uid)

    }
}