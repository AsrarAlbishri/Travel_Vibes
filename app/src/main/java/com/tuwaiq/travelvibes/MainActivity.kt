package com.tuwaiq.travelvibes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.tuwaiq.travelvibes.databinding.ActivityMainBinding
import com.tuwaiq.travelvibes.profileFragment.Language

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentContainerView)

        binding.bottomNavigationView.background = null

        Language.loadLocate(this)



        binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END

        binding.fab.setOnClickListener {

            navController.navigate(R.id.navigation_add)

        }

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.loginFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.fab.visibility = View.GONE
                    binding.bottomAppBar.visibility = View.GONE
                }

                R.id.registerFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.fab.visibility = View.GONE
                    binding.bottomAppBar.visibility = View.GONE

                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE
                    binding.bottomAppBar.visibility = View.VISIBLE
                }
            }
        }

    }
}