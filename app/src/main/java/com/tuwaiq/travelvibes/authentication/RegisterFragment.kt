package com.tuwaiq.travelvibes.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.tuwaiq.travelvibes.MainActivity
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.Registration
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.postListFragment.PostListFragment

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
    private lateinit var loginTV:TextView

    private lateinit var post: Post


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        post = Post()
    }


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

            when(Registration.validation(username,password,email)){
                 Constants.usernameOrPassword -> showToast("please enter valid username or password")
                Constants.digitForPassword -> showToast("please enter valid password")
               Constants.checkEmailPattren -> showToast("please enter valid E-mail")
                Constants.enteredIsCorrect -> registerUser(email =  email, password =  password, username= username )

            }

            val navCon = findNavController()
            val action = RegisterFragmentDirections.actionRegisterFragmentToNavigationHome(post.postId)
            navCon.navigate(action)
        }
    }

    private fun registerUser(username: String, email: String, password: String) {

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