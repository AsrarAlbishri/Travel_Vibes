package com.tuwaiq.travelvibes

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.data.Comment
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "AppRepository"
class AppRepository private constructor(context: Context) {

    val database = FirebaseFirestore.getInstance()

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val postCollectionRef = Firebase.firestore.collection("posts")


    private val fileDir = context.applicationContext.filesDir

    fun getPhotoFile(post: Post): File = File(fileDir , post.photoFileName)


//    fun retrievePerson() = CoroutineScope(Dispatchers.IO).launch {
//        try {
//
//            val querySnapshot = usersCollectionRef.get().await()
//            val sb = StringBuilder()
//            for(document in querySnapshot.documents){
//                val user = document.toObject<User>()
//                sb.append("$user\n")
//            }
//
//        }catch (e:Exception){
//
//        }
//    }



    fun saveUserInfo(user: User)= CoroutineScope(Dispatchers.IO).launch {

        try {
            usersCollectionRef.document(Firebase.auth.currentUser?.uid!!).set(user).await()
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

    suspend fun getFetchPosts(): LiveData<List<Post>>{
        return liveData {
            val posts = mutableListOf<Post>()
             postCollectionRef.get().await()
                .documents.forEach {
                    val post = Post()

                     post.postDescription = it.getString("postDescription").toString()
                     post.location = it.getString("location").toString()
                     post.placeName = it.getString("placeName").toString()
                     post.postImageUrl = it.getString("postImageUrl").toString()
                     post.ownerId = it.getString("id").toString()
                     post.date = it.getString("date").toString()
                     post.restaurant = it.getString("restaurant").toString()
                     post.hotel = it.getString("hotel").toString()
                     post.others = it.getString("others").toString()

                    post.postId = it.id

                    posts.add(post)
                }
            emit(posts)
        }
    }



    suspend fun detailsPost(uid:String) : LiveData<DocumentSnapshot> {


        return liveData {

            val userRef = database.collection("posts")
            val uidRef = userRef.document(uid).get().await()
            Log.e(TAG, "updatePost: ${uidRef.data}")
            emit(uidRef)
        }

        }

    fun updatePost(post: Post )  {

        postCollectionRef.document(post.postId).set(post)

            .addOnSuccessListener {
                Log.e(TAG , "post document update successful ")


            }.addOnFailureListener {
                Log.e(TAG, "Error adding post document")

            }

    }



   fun deletePost(post: Post ) {

            postCollectionRef.document(post.postId).delete()

    }

    fun addComment(comment: Comment , postId: String ) = CoroutineScope(Dispatchers.IO).launch {



        try {
         val oldPost =   postCollectionRef.document(postId).get().await().toObject(Post::class.java)!!
            oldPost.comment += comment


            postCollectionRef.document(postId).update("comment",oldPost.comment).await()
            withContext(Dispatchers.Main){
                Log.d(TAG,"successfully saved comment")
            }

        }catch (e: java.lang.Exception){
            withContext(Dispatchers.Main){
                Log.d(TAG,"reject save comment")
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