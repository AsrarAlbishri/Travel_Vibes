package com.tuwaiq.travelvibes.data

data class Post(

    var postId:String = "",
    var ownerId:String = "",
    var postDescription:String = "",
    var postTitle:String = "",
    var date:String = "",
    var placeName:String = "",
    var location:String = "",
    var restaurant:String = "",
    var hotel:String = "",
    var others:String = "",
    var postImageUrl:String = "",
    var comment:List<Comment> = listOf(),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
      )
  {

    val photoFileName:String
        get() = "IMG$ownerId.jpg"


}




