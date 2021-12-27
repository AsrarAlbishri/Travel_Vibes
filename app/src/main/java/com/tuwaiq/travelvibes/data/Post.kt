package com.tuwaiq.travelvibes.data

data class Post(

    var postId:String = "",
    var ownerId:String = "",
    var postDescription:String = "",
    var date:String = "",
    var placeName:String = "",
    var location:String = "",
    var restaurant:String = "",
    var hotel:String = "",
    var others:String = "",
    var postImageUrl:String = "",
    var comment:List<Comment> = listOf()
      )



  {

    val photoFileName:String
        get() = "IMG$ownerId.jpg"


}




