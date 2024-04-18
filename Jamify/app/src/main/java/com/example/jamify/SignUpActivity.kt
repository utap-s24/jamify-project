package com.example.jamify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jamify.databinding.ActivityMainBinding
import com.example.jamify.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

//tutorial: https://www.youtube.com/watch?v=SpSzRgbhTa4&ab_channel=AndroidKnowledge


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(email.isNotEmpty()) {
                if(password.isNotEmpty()){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(){
                        if (it.isSuccessful){
                            binding.missingEmailPassTextview.text = ""
                            //TODO: does this redirect us to the home frag?
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            //after successful sign up end this activity
                            finish()
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

        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LogInActivity::class.java)
            startActivity(loginIntent)
            finish()
        }


    }
}