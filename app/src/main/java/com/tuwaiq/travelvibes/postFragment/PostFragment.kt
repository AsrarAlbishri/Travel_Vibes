package com.tuwaiq.travelvibes.postFragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.android.gms.maps.model.LatLng
import com.tuwaiq.travelvibes.DatePickerDialogFragment
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.databinding.PostFragmentBinding
import com.tuwaiq.travelvibes.map.LocationResponse
import com.tuwaiq.travelvibes.utils.getScaledBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

private const val TAG = "PostFragment"
private const val REQUEST_CODE_IMAGE_POST = 0
const val POST_DATE_KEY = "post date"

class PostFragment : Fragment() , DatePickerDialogFragment.DatePickerCallback {

    var currentFile: Uri? = null
    var imageRef = Firebase.storage.reference

    var postHolder: Post? = null

    private val args:PostFragmentArgs by navArgs()

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri


    private val postViewModel: PostViewModel by lazy { ViewModelProvider(this)[PostViewModel::class.java] }

    private lateinit var binding: PostFragmentBinding

    private lateinit var post: Post

    private lateinit var firebaseUser: FirebaseUser

    val database = FirebaseFirestore.getInstance()


    private val getPermissionLuncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){

    }

    private val takePhotoLunchr = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){
        if (it){
            photoUri?.let {
                val ref = imageRef.child("postImages/${Calendar.getInstance().time}")
                val uploadImage = ref.putFile(it)
                uploadImage.continueWithTask { task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }

                    ref.downloadUrl
                }

                    .addOnSuccessListener {
                        val imageUri = it.toString()
                        post.postImageUrl = imageUri
                        Log.d(TAG, "imageUri $imageUri" )
                        Toast.makeText(context,"uploaded image", Toast.LENGTH_LONG).show()
                        Firebase.firestore.collection("posts").document(Firebase.auth.currentUser?.uid!!)
                            .update("postImageUrl" , imageUri)

                    }.addOnFailureListener {
                        Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
                    }
            }

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

        LocationResponse.currentLocation = null
        LocationResponse.locationAddress = null

        photoFile = postViewModel.getPhotoFile(post)
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.tuwaiq.travelvibes",
            photoFile

        )

        binding.postCamera.setOnClickListener {

            openCamera()
            uploadImage()


        }

        binding.clickMap.setOnClickListener {
            val action =PostFragmentDirections.actionNavigationAddToMapsFragment()
            findNavController().navigate(action)
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
                post.placeName= placeName.text.toString().lowercase(Locale.getDefault())
//                post.hotel = hotelPlace.isChecked.toString()
//                post.others = othersPlace.isChecked.toString()
//                post.restaurant = restaurantPlace.isChecked.toString()


//                val action = PostFragmentDirections.actionNavigationAddToNavigationHome()
//                findNavController().navigate(action)

            }


            post.ownerId= firebaseUser.uid
            post.postId = UUID.randomUUID().toString()
            postViewModel.savePost(post)
        }




//        binding.postPhoto.setOnClickListener {
//            Intent(Intent.ACTION_GET_CONTENT).also {
//                it.type = "image/*"
//                startActivityForResult(it,REQUEST_CODE_IMAGE_POST)
//            }
//        }

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

        }


//        binding.restaurantPlace.setOnCheckedChangeListener { _, isChecked ->
//            post.restaurant = isChecked.toString()
//        }
//
//        binding.hotelPlace.setOnCheckedChangeListener { _, isChecked ->
//            post.hotel = isChecked.toString()
//        }
//
//        binding.othersPlace.setOnCheckedChangeListener { _, isChecked ->
//            post.others = isChecked.toString()
//        }

        lifecycleScope.launch {
            Log.d(TAG, "onCreateView: ${args.id}")
            postViewModel.detailsPost(args.id).observe(viewLifecycleOwner , androidx.lifecycle.Observer {
                binding.postWrite.setText(it.postDescription)
                binding.placeName.setText(it.placeName)
                if (!it.location.isNullOrEmpty() && it.location != "null"){
                    binding.clickMap.text = it.location
                }

                if (it.latitude != 0.0 && it.longitude != 0.0){
                    postHolder = it
                    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                    mapFragment?.getMapAsync(callback)
                }
//                binding.restaurantPlace.isChecked
//                binding.hotelPlace.isChecked
//                binding.othersPlace.isChecked
                binding.postPhoto.load(it.postImageUrl)

            })
        }

//        binding.addPost.setOnClickListener {
//            postViewModel.updatePost(post)
//        }



        return binding.root
    }

    private val callback = OnMapReadyCallback { googleMap ->
        if (postHolder != null){
            binding.materialCardView.visibility = View.VISIBLE
           val currentLocation = LatLng(postHolder!!.latitude, postHolder!!.longitude)
            googleMap.addMarker(
                MarkerOptions().position(currentLocation).title(postHolder!!.placeName)
                    .snippet(postHolder!!.placeName)
            )

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f))
            googleMap.uiSettings.setAllGesturesEnabled(false)

        }
    }

    private fun uploadImage() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it,REQUEST_CODE_IMAGE_POST)
        }



    }

    override fun onStart() {
        super.onStart()

        binding.datePickerIV.setOnClickListener {
            val args = Bundle()
            args.putSerializable(POST_DATE_KEY,post.date)

            val datePicker = DatePickerDialogFragment()
            datePicker.arguments = args
            datePicker.setTargetFragment(this,0)
            datePicker.show(this.parentFragmentManager,"date picker")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_POST){
            data?.data?.let {
                currentFile = it
                binding.postPhoto.setImageURI(it)
                uploadImageToFirestore()

            }
        }
    }

    private fun uploadImageToFirestore(){

        currentFile?.let {
           val ref = imageRef.child("postImages/${Calendar.getInstance().time}")
               val uploadImage = ref.putFile(it)
                   uploadImage.continueWithTask { task ->
                           if (!task.isSuccessful){
                               task.exception?.let {
                                   throw it
                               }
                           }
                           ref.downloadUrl
                       }
                           .addOnSuccessListener {
                               val imageUri = it.toString()
                               post.postImageUrl = imageUri
                               Log.d(TAG, "imageUri $imageUri" )
                        Toast.makeText(context,"uploaded image", Toast.LENGTH_LONG).show()
                               Firebase.firestore.collection("posts").document(Firebase.auth.currentUser?.uid!!)
                                   .update("postImageUrl",imageUri)


                }.addOnFailureListener {
                    Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
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

    override fun onDateSelected(date: Date) {
         post.date = date.time.toString()
    }

    override fun onResume() {
        super.onResume()

        if (LocationResponse.currentLocation != null && LocationResponse.locationAddress != null){
            binding.clickMap.text = LocationResponse.locationAddress
            post.location = LocationResponse.locationAddress ?: "Current Location Address"
            post.latitude = LocationResponse.currentLocation?.latitude ?: 0.0
            post.longitude = LocationResponse.currentLocation?.longitude ?: 0.0
        }
    }

}