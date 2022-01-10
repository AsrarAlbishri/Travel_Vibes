package com.tuwaiq.travelvibes.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tuwaiq.travelvibes.R
import java.util.*

class MapsFragment : Fragment() {

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var currentLocation : LatLng
    private var locationAddress: String = ""
    private lateinit var addLocationBtn : Button

    private val callback = OnMapReadyCallback { googleMap ->
       this.googleMap = googleMap
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getCurrentLocation()

        addLocationBtn = view.findViewById(R.id.btn_Add_location)
        addLocationBtn.setOnClickListener {
            val addresses : List<Address>
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                addresses = geocoder.getFromLocation(currentLocation.latitude,currentLocation.longitude,1)
                locationAddress = addresses[0].getAddressLine(0)

                LocationResponse.currentLocation = currentLocation
                LocationResponse.locationAddress = locationAddress
                findNavController().popBackStack()
            }catch (e: Exception){
                Log.i("Here" , "No Internet Connection")
            }
        }
    }

    private fun getCurrentLocation(){
        if (ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION)

            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            currentLocation = LatLng(location!!.latitude, location.longitude)
            googleMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location").snippet("Current Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION){
            when (grantResults[0]){
                PackageManager.PERMISSION_GRANTED -> getCurrentLocation()
                PackageManager.PERMISSION_DENIED -> Toast.makeText(requireContext(),"You need to allow GPS Location" ,Toast.LENGTH_LONG).show()
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}