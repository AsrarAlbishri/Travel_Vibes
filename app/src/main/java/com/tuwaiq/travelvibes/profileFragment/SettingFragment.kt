package com.tuwaiq.travelvibes.profileFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.databinding.FragmentProfileEditBinding
import com.tuwaiq.travelvibes.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {


    private lateinit var binding: FragmentSettingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentSettingBinding.inflate(layoutInflater)


        binding.signOutButton.setOnClickListener {

            Firebase.auth.signOut()

            val navCon = findNavController()
            val action = ProfileEditFragmentDirections.actionProfileEditFragmentToLoginFragment()
            navCon.navigate(action)

        }



        return binding.root
    }


}