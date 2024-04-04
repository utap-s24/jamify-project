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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
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

    private fun initMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Inflate the menu; this adds items to the action bar if it is present.
                menuInflater.inflate(R.menu.bottom_app_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_home-> {
                        navController.navigate(R.id.homeFragment)
                        true
                    }
                    R.id.action_add -> {
                        navController.navigate(R.id.createFragment)
                        true
                    }
                    R.id.action_profile -> {
                        navController.navigate(R.id.profileFragment)
                        true
                    }
                    else -> false
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        // Set the layout for the layout we created
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initMenu()

        // Set up our nav graph
        navController = findNavController(R.id.nav_host_fragment)
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        setSupportActionBar(findViewById(R.id.my_toolbar))

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
            progressBarOn()
//            viewModel.fetchPhotoMeta {
//                progressBarOff()
//            }


        }
    }
}