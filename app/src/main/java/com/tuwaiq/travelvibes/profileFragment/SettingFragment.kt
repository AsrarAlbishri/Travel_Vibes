package com.tuwaiq.travelvibes.profileFragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.MainActivity
import com.tuwaiq.travelvibes.R
import com.tuwaiq.travelvibes.databinding.FragmentProfileEditBinding
import com.tuwaiq.travelvibes.databinding.FragmentSettingBinding
import com.tuwaiq.travelvibes.postFragment.PostFragment
import com.tuwaiq.travelvibes.postListFragment.PostListFragment
import java.util.*



private const val PREF_CHANGE_LANG_KEY = "my_lang"
class SettingFragment : Fragment() {


    private lateinit var binding: FragmentSettingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //loadLocate()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loadLocate()

//        val intent = Intent(requireActivity(), PostListFragment::class.java)
//
//        activity?.finish()


        binding= FragmentSettingBinding.inflate(layoutInflater)


        binding.changeLanguage.setOnClickListener {
            showChangeLang()

        }


        binding.signOutButton.setOnClickListener {

            Firebase.auth.signOut()

            val navCon = findNavController()
            val action =SettingFragmentDirections.actionSettingFragmentToLoginFragment()
            navCon.navigate(action)

        }


        return binding.root
    }

    private fun showChangeLang() {
        val listLanguage = arrayOf("Arabic","English")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Language")
        builder.setSingleChoiceItems(listLanguage , -1){ dialog , which ->
           when(which){
               0 -> {setLocate("ar")
               recreate(requireActivity())}

               1 -> {setLocate("en")
               recreate(requireActivity())}
           }
            dialog.dismiss()

        }

        val mDialog = builder.create()
        mDialog.show()
    }


    private fun setLocate(Lang:String){
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale

        context?.resources?.updateConfiguration(config,context?.resources?.displayMetrics)

        getDefaultSharedPreferences(context).edit()
            .putString(PREF_CHANGE_LANG_KEY,Lang)
            .apply()
    }

    private fun loadLocate(){
        val pref = getDefaultSharedPreferences(context)
        val language = pref.getString(PREF_CHANGE_LANG_KEY,"")!!
        setLocate(language)

    }

}