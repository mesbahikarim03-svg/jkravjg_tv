package com.example.myofflineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myofflineapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The welcome message is already set in activity_main.xml via @string/welcome_message
        // You can programmatically change it here if needed:
        // binding.welcomeTextView.text = "Hello from Kotlin!"
    }
}