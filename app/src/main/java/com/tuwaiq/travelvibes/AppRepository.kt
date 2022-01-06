package com.tuwaiq.travelvibes

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.data.*
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
                    val post = it.toObject(Post::class.java)!!
                    post.postId = it.id
                    posts += post

                }
            emit(posts)
        }
    }


    suspend fun getSearchPosts(word:String): LiveData<List<Post>>{
        return liveData {
            val posts = mutableListOf<Post>()
            postCollectionRef.whereEqualTo("placeName", word)
                .get()
                .await()
                .documents.forEach {
                    val post = it.toObject(Post::class.java)!!
                    post.postId = it.id
                    posts += post

                }
            emit(posts)
        }
    }

    suspend fun profilePostData():LiveData<List<Post>>{
        val postList = mutableListOf<Post>()
        Firebase.auth.currentUser?.let {
            Log.d(TAG,it.uid)
            database.collection("posts").whereEqualTo("ownerId",it.uid)
                .get()
                .addOnFailureListener {
                    Log.e(TAG,"!!!!",it)
                }
                .addOnSuccessListener {
                    for (document in it){

                        val post = document.toObject(Post::class.java)
                        Log.d(TAG,"khguy $document" )
                        postList.add(post)

                    }


                }
                .await()

        }
        return liveData {
            emit(postList)
        }
    }

    suspend fun detailsPost(uid:String) : LiveData<Post> {


        return liveData {

            val userRef = database.collection("posts")
            val uidRef = userRef.document(uid).get().await()
            Log.e(TAG, "updatePost: ${uidRef.data}")
            val post = uidRef.toObject(Post::class.java)
            post?.postId = uidRef.id

            if (post != null) {
                emit(post)
            }
        }

        }

//    fun updatePost(post: Post )  {
//
//        postCollectionRef.document(post.postId).set(post)
//
//            .addOnSuccessListener {
//                Log.e(TAG , "post document update successful ")
//
//
//            }.addOnFailureListener {
//                Log.e(TAG, "Error adding post document")
//
//            }
//
//    }



   fun deletePost(post: Post ) {

            postCollectionRef.document(post.postId).delete()
          // postCollectionRef.document(post.postId).update("postId",post)


    }

    fun addComment(comment: Comment , postId: String ) = CoroutineScope(Dispatchers.IO).launch {



        try {
         val oldPost =  postCollectionRef.document(postId).get().await().toObject(Post::class.java)!!
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

    suspend fun getComments(postId: String): LiveData<List<CommentUser>>{

        var comments = mutableListOf<CommentUser>()
//        Log.d(TAG, "getComments: ${x?.comment}")
        /**
         *
         *
         * */

        return liveData {

            val post =    database.collection("posts").document( postId)
            val x = post.get().await().toObject(CommentResponse::class.java)
//            Log.d(TAG, "getComments: ${x?.comment}")
            x?.comment?.forEach {
                var comment = CommentUser()
                comment.comment = it
                comment.user = getUserCommentInfo(it.userId)
                Log.d(TAG, "\ngetComments: ${comment}\n")
                comments += comment
            }
            emit(comments)

            /**
             *
             *
             * */
//          val post =    database.collection("posts").document( postId)
//              val x = post.get().await().toObject(CommentResponse::class.java)
//            Log.d(TAG, "getComments: ${x?.comment}")
//                  .toObjects(Post::class.java)

//            val test: List<Comment> = x.data?.get("comment") as CommentResponse.kotlin.collections.List<Comment> ?: emptyList<Comment>()
//            emit(x?.comment ?: emptyList())
        }

    }

    suspend fun getUserInfo(): LiveData<User> {

                return liveData {

                    val favPost = database.collection("users")
                        .document(Firebase.auth.currentUser?.uid!!)
                    val f = favPost.get().await().toObject(User::class.java)

                    Log.d(TAG, "get favorite :${f?.favorite}")

                    emit(f!!)

                }

    }

    suspend fun getUserCommentInfo(userId: String): User? {
        val user = database.collection("users").document(userId)
        return user.get().await().toObject(User::class.java)
//        return liveData {
//
//            val comment = database.collection("users").document(userId)
//            val f = comment.get().await().toObject(User::class.java)
//
//            Log.d(TAG, "get userComment :${f}")
////            f?.comment?.forEach { comment ->
////                val favPost = database.collection("users")
////                    .document(comment.userId)
////                val dd = favPost.get().await().toObject(User::class.java)
////
////                if (dd != null) {
////                    emit(dd)
////                }
////            }
//            emit(f!!)
//
//        }

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