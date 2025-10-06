package com.example.projectpemmob

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projectpemmob.ui.auth.LoginActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    
    private lateinit var firebaseAuth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        
        // Check if user is already logged in
        checkUserAuthentication()
        
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        
        setupButtons()
    }
    
    private fun checkUserAuthentication() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // User is already logged in, redirect to homepage
            val intent = Intent(this, HomepageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    
    private fun setupButtons() {
        val btnMasuk = findViewById<MaterialButton>(R.id.btnMasuk)
        val btnJelajah = findViewById<MaterialButton>(R.id.btnJelajah)
        
        btnMasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnJelajah.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }
    }
}