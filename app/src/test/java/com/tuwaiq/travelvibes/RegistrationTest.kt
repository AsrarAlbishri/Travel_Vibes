package com.tuwaiq.travelvibes


import com.google.common.truth.Truth.assertThat
import com.tuwaiq.travelvibes.authentication.Constants
import org.junit.Before

import org.junit.Test
import org.testng.annotations.BeforeTest
import java.io.ObjectInputValidation

class RegistrationTest {



    @Test
    fun `username are empty`(){
        val result = Registration.validation(
            "",
            "123",
            "aa@gmail.com"
        )
        assertThat(result).isEqualTo(Constants.usernameOrPassword)
    }


    @Test
    fun `password are empty`(){
        val result = Registration.validation(
            "asrar",
            "",
            "aa@gmail.com"
        )
        assertThat(result).isEqualTo(Constants.usernameOrPassword)
    }

    @Test
    fun `password is less than 2 digit`(){
        val result = Registration.validation(
            "asrar",
            "abcdefg5",
            "aa@gmail.com"
        )
        assertThat(result).isLessThan(Constants.digitForPassword)
    }

    @Test
    fun `checkEmail`(){
        val result = Registration.validation(
            "asrar",
            "123",
            "ssggcom@gg.com"
        )
        assertThat(result).isEqualTo(Constants.checkEmailPattren)
    }


}