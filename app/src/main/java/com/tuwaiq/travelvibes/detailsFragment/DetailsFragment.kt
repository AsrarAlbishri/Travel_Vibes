package com.tuwaiq.travelvibes.detailsFragment

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.commentFragment.CommentViewModel
import com.tuwaiq.travelvibes.data.Post
import com.tuwaiq.travelvibes.databinding.FragmentDetailsBinding
import com.tuwaiq.travelvibes.map.LocationResponse
import com.tuwaiq.travelvibes.postFragment.TAG
import kotlinx.coroutines.launch

private const val TAG = "DetailsFragment"
class DetailsFragment : Fragment() {

    var postHolder: Post? = null

    private val detailsViewModel: DetailsViewModel by lazy { ViewModelProvider(this)[DetailsViewModel::class.java] }

    private lateinit var binding: FragmentDetailsBinding

    private val args:DetailsFragmentArgs by navArgs()

    private lateinit var post: Post



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = Post()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailsBinding.inflate(layoutInflater)

        lifecycleScope.launch {
            Log.d(TAG, "onCreateView: ${args.id}")
            detailsViewModel.detailsPost(args.id).observe(viewLifecycleOwner , androidx.lifecycle.Observer {
                binding.postDescription.setText(it.postDescription)
                binding.placeNameTv.setText(it.placeName)
                binding.locationAddressDetails.setText(it.location)


                if (it.latitude != 0.0 && it.longitude != 0.0){
                    postHolder = it
                    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                    mapFragment?.getMapAsync(callback)
                }


                binding.detailsIV.load(it.postImageUrl)

            })
        }

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



}