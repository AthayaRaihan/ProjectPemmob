package com.example.projectpemmob.ui.profil

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import android.widget.ImageView
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.projectpemmob.MainActivity
import com.example.projectpemmob.R
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.wisata.WisataActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        initViews()

        // Setup data
        setupUserData()

    // Setup bottom navigation (shared handler)
    com.example.projectpemmob.ui.navigation.BottomNavigationHandler(this).setupBottomNavigation()
    // Ensure no transition animation so bottom nav appears static when returning
    overridePendingTransition(0, 0)

        // Setup click listeners
        setupClickListeners()
    }

    override fun onStart() {
        super.onStart()
        // Refresh UI when activity becomes visible in case auth state changed
        setupUserData()
    }

    private fun initViews() {
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        tvUserName = findViewById(R.id.tv_user_name)
        tvUserEmail = findViewById(R.id.tv_user_email)
        // login button/card for unauthenticated users
        val loginCard = findViewById<androidx.cardview.widget.CardView>(R.id.login_card)
        val btnLogin = findViewById<TextView>(R.id.btn_login)
        btnLogin.setOnClickListener {
            val intent = Intent(this, com.example.projectpemmob.ui.auth.LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupUserData() {
        // Use ViewModel (MVVM) to load user data and observe
        val currentUser = FirebaseAuth.getInstance().currentUser
        val scrollContent = findViewById<android.widget.ScrollView>(R.id.scroll_profile_content)
        val loginCard = findViewById<androidx.cardview.widget.CardView>(R.id.login_card)

        if (currentUser == null) {
            // Show only login card and hide scroll content
            loginCard.visibility = android.view.View.VISIBLE
            scrollContent.visibility = android.view.View.GONE
            // clear fields
            tvUserName.text = ""
            tvUserEmail.text = ""
            etName.setText("")
            etEmail.setText("")
            return
        } else {
            // Show profile content
            loginCard.visibility = android.view.View.GONE
            scrollContent.visibility = android.view.View.VISIBLE
        }

    val uid = currentUser.uid
        viewModel.user.observe(this, Observer { user ->
            if (user != null) {
                tvUserName.text = user.name ?: currentUser.displayName ?: ""
                tvUserEmail.text = user.email ?: currentUser.email ?: ""
                etName.setText(user.name ?: currentUser.displayName ?: "")
                etEmail.setText(user.email ?: currentUser.email ?: "")
            } else {
                // fallback
                tvUserName.text = currentUser.displayName ?: ""
                tvUserEmail.text = currentUser.email ?: ""
                etName.setText(currentUser.displayName ?: "")
                etEmail.setText(currentUser.email ?: "")
            }
        })

        viewModel.error.observe(this, Observer { e ->
            if (e != null) {
                Log.w("ProfileActivity", "Error loading user: ${e.message}")
            }
        })

        // Trigger load
        viewModel.loadUser(uid)

        // Set profile image if available from Firebase user
        try {
            val profileImage = findViewById<ImageView>(R.id.profile_image)
            val photoUri = currentUser.photoUrl
            if (photoUri != null) {
                profileImage.setImageURI(photoUri)
            } else {
                // keep default icon
            }
        } catch (e: Exception) {
            // ignore image loading issues
        }
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
        findViewById<LinearLayout>(R.id.ll_destinasi_favorit)?.setOnClickListener {
            val intent = Intent(this, FavoritActivity::class.java)
            startActivity(intent)
        }

        // Keluar (Logout) button
        findViewById<TextView>(R.id.btn_keluar)?.setOnClickListener {
            // Show confirmation dialog before logging out
            AlertDialog.Builder(this)
                .setTitle("Keluar")
                .setMessage("Yakin ingin keluar dari akun?")
                .setPositiveButton("OK") { _, _ ->
                    // Sign out from Firebase
                    FirebaseAuth.getInstance().signOut()

                    // Also sign out from Google so next login prompts account chooser
                    try {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleClient = GoogleSignIn.getClient(this, gso)
                        googleClient.signOut().addOnCompleteListener {
                            // Navigate to first screen (MainActivity) and clear back stack
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    } catch (e: Exception) {
                        // Fallback: at least navigate away even if Google sign out fails
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    // Navigation handled by BottomNavigationHandler
}