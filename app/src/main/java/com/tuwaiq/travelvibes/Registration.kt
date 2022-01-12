package com.tuwaiq.travelvibes

import com.tuwaiq.travelvibes.authentication.Constants
import com.tuwaiq.travelvibes.authentication.Constants.EMAIL_PATTERN

object Registration {



    fun validation (
        userName : String ,
        password : String,
       email : String
    ):String {
        if (userName.isEmpty() || password.isEmpty()){
            return Constants.usernameOrPassword
        }

        if (password.count { it.isDigit() } < 2){
            return  Constants.digitForPassword
        }

        if (!email.matches(EMAIL_PATTERN.toRegex())){
           return Constants.checkEmailPattren
        }
      return Constants.enteredIsCorrect
    }
}