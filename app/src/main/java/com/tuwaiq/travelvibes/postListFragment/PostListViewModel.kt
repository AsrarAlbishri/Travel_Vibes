package com.tuwaiq.travelvibes.postListFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tuwaiq.travelvibes.AppRepository
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User

class PostListViewModel : ViewModel() {

    private val repo: AppRepository = AppRepository.getInstance()

    suspend fun getFetchPosts(): LiveData<List<Post>> {
        return repo.getFetchPosts()

    }

    fun deletePost(post: Post ){
         repo.deletePost(post)
    }

    suspend fun getSearchPosts(word:String): LiveData<List<Post>>{
        return repo.getSearchPosts(word)
    }

    suspend fun getUserInfo(uid:String):LiveData<User>{
      return  repo.getUserInfo(uid)
    }


}