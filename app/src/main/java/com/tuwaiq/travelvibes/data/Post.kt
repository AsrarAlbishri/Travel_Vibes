package com.tuwaiq.travelvibes.data

import java.util.*

data class Post (

    var id:String ,
    var postDescription:String,
   // var date: Date = Date(),
    var placeName:String ,
    var location:String ,
    var restaurant:Boolean ,
    var hotel:Boolean  ,
    var others:Boolean ,
    var img:String ){

    constructor():this("","","","",
        false,false,false,"")
}




