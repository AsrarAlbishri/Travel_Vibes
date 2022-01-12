package com.tuwaiq.travelvibes


import com.google.common.truth.Truth.assertThat
import org.junit.Before

import org.junit.Test
import org.testng.annotations.BeforeTest
import java.io.ObjectInputValidation

class RegistrationTest {

//    private lateinit var validation: Registration
//
//    @BeforeTest
//    fun setUp(){
//        validation = Registration
//    }

    @Test
    fun `empty username returns false`(){
        val result = Registration.validation(
            "",
            "123"

        )
        assertThat(result).isFalse()
    }

    @Test
    fun `valid username and correctly repeated password returns true`(){
        val result = Registration.validation(
            "asrar",
            "123"

        )
        assertThat(result).isTrue()
    }

    @Test
    fun `username already exists returns false`(){
        val result = Registration.validation(
            "asrar.albishri",
            "123"

        )
        assertThat(result).isFalse()
    }

//    @Test
//    fun `incorrectly confirmed password returns false`(){
//        val result = Registration.validation(
//            "asrar",
//            "123456",
//            "123"
//        )
//        assertThat(result).isFalse()
//    }

    @Test
    fun `empty password returns false`(){
        val result = Registration.validation(
            "asrar",
            ""

        )
        assertThat(result).isFalse()
    }
}