package com.tuwaiq.travelvibes.profileFragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {


    private lateinit var binding: FragmentSettingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Language.loadLocate(requireActivity())


        binding = FragmentSettingBinding.inflate(layoutInflater)


        binding.changeLanguage.setOnClickListener {
            Language.showChangeLang(requireActivity())

        }

        binding.signOutButton.setOnClickListener {

            Firebase.auth.signOut()

            val navCon = findNavController()
            val action = SettingFragmentDirections.actionSettingFragmentToLoginFragment()
            navCon.navigate(action)

        }

        return binding.root
    }


}