package com.tuwaiq.travelvibes.data

import java.util.*

data class Post (

    var postId:String = "",
    var id:String = "" ,
    var postDescription:String = "",
    var date:String = "",
    var placeName:String = "",
    var location:String = "" ,
    var restaurant:String = "" ,
    var hotel:String = "" ,
    var others:String = "",
    var postImageUrl:String = "")

  {

    val photoFileName:String
        get() = "IMG$id.jpg"


}




