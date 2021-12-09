package com.tuwaiq.travelvibes.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.tuwaiq.travelvibes.R

class LoginFragment : Fragment() {

    private lateinit var userNameET: EditText
    private lateinit var passwordET: EditText
    private lateinit var loginBtn: Button
    private lateinit var signUp:TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        userNameET = view.findViewById(R.id.username_et)
        passwordET = view.findViewById(R.id.password_et)
        loginBtn = view.findViewById(R.id.login_btn)

        view.findViewById<Button>(R.id.register_btn).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(),false)
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        loginBtn.setOnClickListener {
            RegisterFragment.auth.signInWithEmailAndPassword(
                userNameET.text.toString(),
                passwordET.text.toString()
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("login successful")
                    } else {
                        showToast("login failed")
                    }
                }
        }






    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}


