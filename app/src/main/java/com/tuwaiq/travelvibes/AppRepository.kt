package com.tuwaiq.travelvibes

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.StringBuilder

private const val TAG = "AppRepository"
class AppRepository private constructor(context: Context) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val postCollectionRef = Firebase.firestore.collection("posts")


    private val fileDir = context.applicationContext.filesDir

    fun getPhotoFile(post: Post): File = File(fileDir , post.photoFileName)


    fun retrievePerson() = CoroutineScope(Dispatchers.IO).launch {
        try {

            val querySnapshot = usersCollectionRef.get().await()
            val sb = StringBuilder()
            for(document in querySnapshot.documents){
                val user = document.toObject<User>()
                sb.append("$user\n")
            }

        }catch (e:Exception){

        }
    }



    fun saveUserInfo(user: User)= CoroutineScope(Dispatchers.IO).launch {

        try {
            usersCollectionRef.add(user).await()
            withContext(Dispatchers.Main){
                Log.d(TAG,"successfully saved data")
            }


        }catch (e:Exception) {
            withContext(Dispatchers.Main){
                Log.d(TAG,"reject save data")
            }


        }

    }

    fun addPost(post: Post) = CoroutineScope(Dispatchers.IO).launch {
        try {
            postCollectionRef.add(post).await()
            withContext(Dispatchers.Main){
                Log.d(TAG,"successfully saved post")
            }

        }catch (e: java.lang.Exception){
            withContext(Dispatchers.Main){
                Log.d(TAG,"reject save post")
            }
        }
    }


    companion object{
        private var INSTANCE:AppRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = AppRepository(context)
            }
        }

        fun getInstance():AppRepository = INSTANCE ?: throw IllegalStateException("you must initialize your repo")


    }


}