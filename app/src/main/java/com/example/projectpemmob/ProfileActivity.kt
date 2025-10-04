package com.example.projectpemmob

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Initialize views
        initViews()
        
        // Setup data
        setupUserData()
        
        // Setup bottom navigation
        setupBottomNavigation()
        
        // Setup click listeners
        setupClickListeners()
    }
    
    private fun initViews() {
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        tvUserName = findViewById(R.id.tv_user_name)
        tvUserEmail = findViewById(R.id.tv_user_email)
    }
    
    private fun setupUserData() {
        // Set default user data (in real app, this would come from preferences or database)
        val userName = "Rafif Surya Murtadha"
        val userEmail = "rafifsurya@gmail.com"
        
        tvUserName.text = userName
        tvUserEmail.text = userEmail
        etName.setText("Rafif Surya")
        etEmail.setText(userEmail)
    }
    
    private fun setupClickListeners() {
        // Reset Password button
        findViewById<TextView>(R.id.btn_reset_password)?.setOnClickListener {
            // TODO: Implement reset password functionality
            // For now, just show a toast or placeholder
        }
        
        // Edit button
        findViewById<TextView>(R.id.btn_edit)?.setOnClickListener {
            // TODO: Implement edit profile functionality
            // Enable/disable editing, save changes, etc.
        }
        
        // Destinasi Favorit
        findViewById<android.widget.LinearLayout>(R.id.ll_destinasi_favorit)?.setOnClickListener {
            val intent = Intent(this, FavoritActivity::class.java)
            startActivity(intent)
        }
        
        // Keluar (Logout) button
        findViewById<TextView>(R.id.btn_keluar)?.setOnClickListener {
            // TODO: Implement logout functionality
            // Clear preferences, go to login screen, etc.
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    
    private fun setupBottomNavigation() {
        try {
            // Icon Home untuk kembali ke homepage
            findViewById<android.widget.LinearLayout>(R.id.ll_home)?.setOnClickListener {
                val intent = Intent(this, HomepageActivity::class.java)
                startActivity(intent)
                finish()
            }
            
            // Icon Location untuk ke halaman wisata
            findViewById<android.widget.LinearLayout>(R.id.ll_location)?.setOnClickListener {
                val intent = Intent(this, WisataActivity::class.java)
                startActivity(intent)
                finish()
            }
            
            // Icon Favorites untuk ke halaman favorit
            findViewById<android.widget.LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                val intent = Intent(this, FavoritActivity::class.java)
                startActivity(intent)
                finish()
            }
            
            // Icon Restaurant untuk ke halaman kuliner
            findViewById<android.widget.LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                val intent = Intent(this, KulinerActivity::class.java)
                startActivity(intent)
                finish()
            }
            
            // Icon Profile - sudah di halaman profile, tidak perlu action
            findViewById<android.widget.LinearLayout>(R.id.ll_profile)?.setOnClickListener {
                // Sudah di halaman profile, tidak perlu navigasi
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}