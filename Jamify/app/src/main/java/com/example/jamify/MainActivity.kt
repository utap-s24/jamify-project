package com.example.jamify

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.jamify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authUser : AuthUser
    private val viewModel: MainViewModel by viewModels()
    companion object {
        const val TAG = "MainActivity"
    }

    fun progressBarOn() {
        binding.indeterminateBar.visibility = View.VISIBLE
    }

    fun progressBarOff() {
        binding.indeterminateBar.visibility = View.GONE
    }

//    private fun initMenu() {
//        addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                // Inflate the menu; this adds items to the action bar if it is present.
//                menuInflater.inflate(R.menu.settings, menu)
//            }
//
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return when (menuItem.itemId) {
//                    R.id.action_home->navigate.
//                    R.id.action_add->setCurrentFragment(secondFragment)
//                    R.id.action_profile->setCurrentFragment(thirdFragment)
////
//                }
//            }
//        })
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")



        // Set the layout for the layout we created
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        initMenu()
        // source for setting up nav:
        // https://tiwiz.medium.com/navigation-component-bottom-navigation-and-how-to-make-them-behave-36dda237850
        navController = findNavController(R.id.nav_host_fragment)
        binding.bottonnav.setupWithNavController(navController)

//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)


        // No need to override onSupportNavigateUp(), because no up navigation


    }


    // We can only safely initialize AuthUser once onCreate has completed.
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        // Create authentication object.  This will log the user in if needed
        authUser = AuthUser(activityResultRegistry)
        // authUser needs to observe our lifecycle so it can run login activity
        lifecycle.addObserver(authUser)

        authUser.observeUser().observe(this) { user->
            // XXX Write me, user status has changed
            viewModel.setCurrentAuthUser(user)
//            progressBarOn()
//            viewModel.fetchPhotoMeta {
//                progressBarOff()
//            }


        }
    }
}