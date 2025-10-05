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
    
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // SEMENTARA NONAKTIFKAN AUTO-LOGIN UNTUK TESTING
        // checkUserLoggedIn()
        
        // Log current user status
        val currentUser = auth.currentUser
        if (currentUser != null) {
            android.util.Log.d("MainActivity", "User logged in: ${currentUser.email}")
        } else {
            android.util.Log.d("MainActivity", "No user logged in")
        }

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
        
        // TOMBOL LOGOUT TERLIHAT - untuk debugging
        setupLogoutButton()
    }
    
    private fun checkUserLoggedIn() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            android.util.Log.d("MainActivity", "User already logged in: ${currentUser.uid}")
            android.util.Log.d("MainActivity", "User email: ${currentUser.email}")
            android.util.Log.d("MainActivity", "User displayName: ${currentUser.displayName}")
            
            // Tambahkan delay kecil untuk memastikan UI ready
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                // User is already logged in, redirect to homepage
                val intent = Intent(this, HomepageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }, 100)
        } else {
            android.util.Log.d("MainActivity", "No user logged in, staying on main screen")
        }
    }
    
    private fun performLogout() {
        android.util.Log.d("MainActivity", "Performing logout...")
        
        // Sign out from Firebase
        auth.signOut()
        
        android.util.Log.d("MainActivity", "Firebase Auth sign out completed")
        
        // Show toast message
        android.widget.Toast.makeText(this, "Berhasil logout!", android.widget.Toast.LENGTH_SHORT).show()
        
        // Refresh activity to show login screen
        recreate()
    }
    
    private fun setupLogoutButton() {
        // Buat tombol logout yang lebih terlihat
        val logoutButton = android.widget.Button(this)
        logoutButton.text = "FORCE LOGOUT"
        logoutButton.setBackgroundColor(android.graphics.Color.RED)
        logoutButton.setTextColor(android.graphics.Color.WHITE)
        
        logoutButton.setOnClickListener {
            android.util.Log.d("MainActivity", "Force logout clicked")
            performLogout()
        }
        
        // Tambahkan ke root layout
        try {
            val rootView = findViewById<android.view.ViewGroup>(android.R.id.content)
            val mainLayout = rootView.getChildAt(0) as? android.view.ViewGroup
            
            if (mainLayout != null) {
                val params = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(32, 32, 32, 32)
                logoutButton.layoutParams = params
                
                mainLayout.addView(logoutButton)
                android.util.Log.d("MainActivity", "Logout button added successfully")
            }
        } catch (e: Exception) {
            android.util.Log.w("MainActivity", "Could not add logout button", e)
        }
    }
}