package com.tuwaiq.travelvibes.profileFragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.databinding.FragmentProfileEditBinding
import kotlinx.coroutines.launch
import java.util.*


private const val REQUEST_CODE = 0
private const val TAG = "ProfileEditFragment"

class ProfileEditFragment : Fragment() {

    var currentFile: Uri? = null
    var imageRef = Firebase.storage.reference


    private val profileViewModel: ProfileViewModel by lazy { ViewModelProvider(this)[ProfileViewModel::class.java] }
    private lateinit var binding: FragmentProfileEditBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = User()
        auth = FirebaseAuth.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileEditBinding.inflate(layoutInflater)

        binding.save.setOnClickListener {

            binding.apply {
                user.firstName = firstNameEdit.text.toString()
                user.lastName = lastNameEdit.text.toString()
                user.userName = userNameEdit.text.toString()
                user.email = emailEdit.text.toString()
                user.phoneNumber = phoneNum.text.toString()
                user.bio = editBio.text.toString()

                uploadImageToFirebase()

            }

            profileViewModel.saveUser(user)

            if (!user.profileImageUrl.isNullOrEmpty()) {
                uploadImageToFirebase()
            }
        }



        binding.uploadimageButton.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE)
            }
            uploadImageToFirebase()
        }


        lifecycleScope.launch {
            profileViewModel.getUserInfo(Firebase.auth.currentUser?.uid!!)
                .observe(viewLifecycleOwner, {
                    user = it
                    binding.firstNameEdit.setText(it.firstName)
                    binding.lastNameEdit.setText(it.lastName)
                    binding.userNameEdit.setText(it.userName)
                    binding.phoneNum.setText(it.phoneNumber)
                    binding.emailEdit.setText(it.email)
                    binding.editBio.setText(it.bio)
                    binding.profileImage.load(it.profileImageUrl)

                })
        }

        return binding.root
    }


    private fun uploadImageToFirebase() {
        currentFile?.let {
            val ref = imageRef.child("images/${Calendar.getInstance().time}")
            val uploadImge = ref.putFile(it)
            uploadImge.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl

            }
                .addOnSuccessListener {

                    val imageUri = it.toString()
                    user.profileImageUrl = imageUri
                    Log.d(TAG, "image url $imageUri")
                    Firebase.firestore.collection("users")
                        .document(Firebase.auth.currentUser?.uid!!)
                        .update("profileImageUrl", imageUri)

                }

                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            data?.data?.let {
                currentFile = it
                binding.profileImage.setImageURI(it)

            }
        }
    }

}