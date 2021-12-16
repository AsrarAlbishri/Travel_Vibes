package com.tuwaiq.travelvibes.profileFragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.databinding.FragmentProfileEditBinding
import com.tuwaiq.travelvibes.postFragment.PostFragmentDirections


private const val REQUEST_CODE = 0
private const val TAG = "ProfileEditFragment"
class ProfileEditFragment : Fragment() {

    var currentFile: Uri? = null
    var imageRef = Firebase.storage.reference

    private val  profileViewModel: ProfileViewModel by lazy { ViewModelProvider(this)[ProfileViewModel::class.java] }

    private lateinit var binding: FragmentProfileEditBinding

    private lateinit var user: User



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user=User()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentProfileEditBinding.inflate(layoutInflater)

        binding.save.setOnClickListener {

            binding.apply {
                user.firstName=firstNameEdit.text.toString()
                user.lastName=lastNameEdit.text.toString()
                user.userName=userNameEdit.text.toString()
                user.email=emailEdit.text.toString()
                user.phoneNumber=phoneNum.text.toString()

        }

            profileViewModel.saveUser(user)
        }


        binding.profileImage.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it,REQUEST_CODE)
            }
        }

        binding.uploadButton.setOnClickListener {
            uploadImageToFirebase("myImage")
        }

        binding.retrieveButton.setOnClickListener {
            downloadImage("myImage")
        }

        return binding.root
    }

    private fun downloadImage(fileName: String){
        val maxDownloadSize = 5L * 1024 * 1024
        val bytes = imageRef.child("images/$fileName").getBytes(maxDownloadSize)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    val bitmap = BitmapFactory.decodeByteArray(task.result,0,task.result!!.size)

                    binding.profileImage.setImageBitmap(bitmap)
                }

            }.addOnFailureListener {
                Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageToFirebase(fileName:String){
        currentFile?.let {
           val ref = imageRef.child("images/$fileName")
            val uploadImge =   ref.putFile(it)
            val imgUrl= uploadImge.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl

            }
                .addOnSuccessListener{

                        val imageUri = it.toString()
                        user.profileImageUrl=imageUri
                    Log.d(TAG,"image url $imageUri")
                        Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
                            .update("profileImageUrl" , imageUri)

                }

                .addOnFailureListener{
                    Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
                }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            data?.data?.let {
                currentFile = it
                binding.profileImage.setImageURI(it)

            }
        }
    }




}