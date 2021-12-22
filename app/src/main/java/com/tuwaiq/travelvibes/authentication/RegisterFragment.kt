package com.tuwaiq.travelvibes.authentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.data.User

private const val TAG = "RegisterFragment"
class RegisterFragment : Fragment() {



    companion object {
        var auth: FirebaseAuth = FirebaseAuth.getInstance()


    }

    private lateinit var usernameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var registerBtn: Button
    private lateinit var loginTV:Button

   // private lateinit var auth:FirebaseAuth

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        auth = FirebaseAuth.getInstance()
//    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if (currentUser != null){
            loginTV.setOnClickListener {
                val navCon = findNavController()
                val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                navCon.navigate(action)
            }
        }

        registerBtn.setOnClickListener {
            val username:String = usernameET.text.toString()
            val email:String = emailET.text.toString()
            val password:String = passwordET.text.toString()
            val confirmPassword:String = confirmPassword.text.toString()

            when{
                username.isEmpty() -> showToast("please enter valid username")
                email.isEmpty()-> showToast("please enter valid E-mail")
                password.isEmpty() -> showToast("please enter valid password")
                password != confirmPassword -> showToast("password doesn't match password confirmation")

                else -> {
                    registerUser(email =  email, password =  password, username= username )
                }

            }

        }
    }


    private fun registerUser(username: String, email: String, password: String,) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val user = User(userName = username, email = email)

//                    user.userName = username
//                    user.email = email

                    val firestoreDB = FirebaseFirestore.getInstance()
                    firestoreDB.collection("users").document(auth.currentUser!!.uid).set(user)

                    showToast("register successful")

                } else {
                    Log.e(TAG, "there was something wrong", task.exception)
                }
            }

                    val updateProfile = userProfileChangeRequest {
                        displayName = username


                    }

                    auth.currentUser?.updateProfile(updateProfile)

                }


    private fun showToast(message:String){
        Toast.makeText(requireContext(),message, Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)

        initialization(view)

        return view
    }

    private fun initialization(view: View) {
        usernameET = view.findViewById(R.id.username_et)
        emailET = view.findViewById(R.id.email_et)
        passwordET = view.findViewById(R.id.password_et)
        confirmPassword = view.findViewById(R.id.confirm_password_et)
        registerBtn = view.findViewById(R.id.register_btn)
        loginTV = view.findViewById(R.id.logintv)
    }


}