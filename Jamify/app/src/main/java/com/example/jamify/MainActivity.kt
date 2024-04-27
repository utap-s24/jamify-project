package com.example.jamify

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.jamify.com.example.jamify.APIInterface
import com.example.jamify.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth
    private  var user: FirebaseUser? = null
//    private lateinit var authUser : AuthUser
    private val viewModel: MainViewModel by viewModels()
    companion object {
        const val TAG = "MainActivity"
    }

    // An Android nightmare
    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    // https://stackoverflow.com/questions/7789514/how-to-get-activitys-windowtoken-without-view
    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
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
//                menuInflater.inflate(R.menu.settings, menu)

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.homeFragment -> {
//                        menuItem.iconTintList(arrayListOf(R.color.logo_purple, R.color.purple))

                        navController.navigate(R.id.homeFragment)
                        true
                    }
                    R.id.profileFragment -> {
                        navController.navigate(R.id.profileFragment)
                        true
                    }
                    R.id.createFragment -> {

                        navController.navigate(R.id.createFragment)
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
//        initMenu()
        // source for setting up nav:
        // https://tiwiz.medium.com/navigation-component-bottom-navigation-and-how-to-make-them-behave-36dda237850
        navController = findNavController(R.id.nav_host_fragment)
        binding.bottonnav.setupWithNavController(navController)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        if (user == null){
            //nav to Log in
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.root.setOnTouchListener { _, event ->
            hideKeyboard()
            false
        }




    }


    // We can only safely initialize AuthUser once onCreate has completed.
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        // Create authentication object.  This will log the user in if needed
//        authUser = AuthUser(activityResultRegistry)
//        // authUser needs to observe our lifecycle so it can run login activity
//        lifecycle.addObserver(authUser)


        //oki lets see if we set up own sign up properly

        // Check if there's a current user
//        val currentUser = ""
//        if (currentUser != null) {
//            // Navigate to home fragment if user is logged in
////            navigateToHomeFragment()
//        } else {
//            // Navigate to sign-up screen if no user is logged in
////            navigateToSignUpScreen()
//        }


//
//        }
    }

    // navigateUp:
    // If we came here from within the app, pop the back stack
    // If we came here from another app, return to it.
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}