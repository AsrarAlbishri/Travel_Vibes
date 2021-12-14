package com.tuwaiq.travelvibes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.tuwaiq.travelvibes.authentication.FragmentNavigation
import com.tuwaiq.travelvibes.authentication.LoginFragment
import com.tuwaiq.travelvibes.databinding.ActivityMainBinding
import com.tuwaiq.travelvibes.profileFragment.ProfileEditFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationView

        val navController = findNavController(R.id.container)

        bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController,AppBarConfiguration(setOf(R.layout.post_list_fragment,R.layout.post_fragment,R.layout.profile_fragment)))


//        supportFragmentManager.beginTransaction()
//            .add(R.id.container_fragment, ProfileEditFragment())
//            .commit()
    }
//
//    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
//         val transaction = supportFragmentManager
//             .beginTransaction()
//             .replace(R.id.container_fragment,fragment)
//
//        if (addToStack){
//            transaction.addToBackStack(null)
//        }
//
//        transaction.commit()
//    }
}