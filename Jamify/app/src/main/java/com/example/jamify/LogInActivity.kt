package com.example.jamify

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jamify.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

//tutorial: https://www.youtube.com/watch?v=SpSzRgbhTa4&ab_channel=AndroidKnowledge


class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            //after successful sign up end this activity
            finish()

        }


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEditText.text.toString()
            val password = binding.loginPasswordEditText.text.toString()

            if(email.isNotEmpty()) {
                if(password.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(){
                        if (it.isSuccessful){
                            binding.missingEmailPassTextview.text = ""
                            //TODO: does this redirect us to the home frag?
//                            val intent = Intent(this, MainActivity::class.java)
//                            startActivity(intent)
//                            //after successful sign up end this activity
//                            finish()
                        } else {
                            binding.missingEmailPassTextview.text = it.exception.toString()
                        }
                    }
                } else {
                    binding.missingEmailPassTextview.text = "Must provide a valid password"
                }
            } else {
                binding.missingEmailPassTextview.text = "Must enter a valid email"
            }
        }

        binding.signupRedirectText.setOnClickListener {
            val loginIntent = Intent(this, SignUpActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

//        val signUpTextView = binding.signupRedirectText
//// Create a SpannableString
//        val spannableString = SpannableString(signUpTextView.text)
//
//// Create a ClickableSpan for the "Sign Up" part
//        val clickableSpan = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                // Handle the click action, e.g., navigate to the sign-up activity
//                val signUpIntent = Intent(this@LogInActivity, SignUpActivity::class.java)
//                startActivity(signUpIntent)
//            }
//        }
//
//// Set the ClickableSpan on the "Sign Up" part
//        val startIndex = spannableString.indexOf("Sign Up")
//        val endIndex = startIndex + "Sign Up".length
//        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0)
//
//// Set the modified SpannableString to the TextView
//        signUpTextView.text = spannableString
//        signUpTextView.movementMethod = LinkMovementMethod.getInstance()


    }
}