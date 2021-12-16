package com.tuwaiq.travelvibes.postFragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tuwaiq.travelvibes.authentication.LoginFragmentDirections
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.data.User
import com.tuwaiq.travelvibes.databinding.PostFragmentBinding
import com.tuwaiq.travelvibes.utils.getScaledBitmap
import java.io.File

private const val TAG = "PostFragment"

private const val REQUEST_CODE_IMAGE_POST = 0

class PostFragment : Fragment() {

    var currentFile: Uri? = null
    var imageRef = Firebase.storage.reference

    private lateinit var photoFile: File
    private lateinit var photoUri:Uri


    private val postViewModel: PostViewModel by lazy { ViewModelProvider(this)[PostViewModel::class.java] }

    private lateinit var binding: PostFragmentBinding

    private lateinit var post: Post

    private lateinit var firebaseUser: FirebaseUser


    private val getPermissionLuncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){

    }

    private val takePhotoLunchr = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){
        if (it){
            updateImageview()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Firebase.auth.currentUser != null){
            firebaseUser = Firebase.auth.currentUser!!
        }else{

            val navCon = findNavController()
            val action = PostFragmentDirections.actionNavigationAddToRegisterFragment()
            navCon.navigate(action)
        }


        binding= PostFragmentBinding.inflate(layoutInflater)

        post = Post()

        photoFile = postViewModel.getPhotoFile(post)
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.tuwaiq.travelvibes",
            photoFile

        )

        binding.postCamera.setOnClickListener {

            openCamera()

        }

    }

    private fun openCamera() {
        if (PackageManager.PERMISSION_GRANTED == context?.let {
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            }) {
            takePhotoLunchr.launch(photoUri)

        } else {
            getPermissionLuncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


       // binding= PostFragmentBinding.inflate(layoutInflater)

        binding.addPost.setOnClickListener {
            binding.apply {
                post.postDescription=postWrite.text.toString()
                post.placeName=placeName.text.toString()
            }


            post.id = firebaseUser.uid
            postViewModel.savePost(post)
        }


        binding.postPhoto.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it,REQUEST_CODE_IMAGE_POST)
            }
        }

        binding.postCamera.setOnClickListener {
            val builder = context?.let { it -> AlertDialog.Builder (it) }
            builder?.let {
                val pictureDialogItems = arrayOf("Choose from Gallery", "Capture a photo")
                    builder.setItems(pictureDialogItems){
                        dialog , which ->
                        when (which){
                            0 -> uploadImage()
                            1 -> openCamera()
                        }
                    }
               val alert = builder.create()
                alert.show()
            }

          // uploadImage("ImageOfPost")
        }

        binding.restaurantPlace.setOnCheckedChangeListener { _, isChecked ->
            post.restaurant = isChecked
        }

        binding.hotelPlace.setOnCheckedChangeListener { _, isChecked ->
            post.hotel = isChecked
        }

        binding.othersPlace.setOnCheckedChangeListener { _, isChecked ->
            post.others = isChecked
        }

        return binding.root
    }

    private fun uploadImage() {
        // هنا احط كود القاليري

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_POST){
            data?.data?.let {
                currentFile = it
                binding.postPhoto.setImageURI(it)

                currentFile?.let {
                    imageRef.child("postImages/ImageOfPost").putFile(it)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                Toast.makeText(context,"uploaded image", Toast.LENGTH_LONG).show()
                            }

                        }.addOnFailureListener {
                            Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
                        }

                }

            }
        }
    }

    fun updateImageview(){
        if (photoFile.exists()){
            val bitmap = getScaledBitmap(photoFile.path,requireActivity())
            binding.postPhoto.setImageBitmap(bitmap)
        }else {
             binding.postPhoto.setImageBitmap(null)
        }

    }

}