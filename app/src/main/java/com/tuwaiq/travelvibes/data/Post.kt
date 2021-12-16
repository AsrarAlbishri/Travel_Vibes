package com.tuwaiq.travelvibes.data

import java.util.*

data class Post (


    var id:String = "" ,
    var postDescription:String = "",
    var date: Date = Date(),
    var placeName:String = "",
    var location:String = "" ,
    var restaurant:Boolean = false ,
    var hotel:Boolean = false ,
    var others:Boolean = false, )

  {

    val photoFileName:String
        get() = "IMG$id.jpg"


}




