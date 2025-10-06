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
    private lateinit var btnEdit: TextView
    private val viewModel: ProfileViewModel by viewModels()
    
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        initViews()

        // Setup data
        setupUserData()
        
        // Setup observers
        setupObservers()

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
        btnEdit = findViewById(R.id.btn_edit)
        
        
        // Initially disable editing
        setEditMode(false)
        
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
                val displayName = user.name ?: currentUser.displayName ?: ""
                val displayEmail = user.email ?: currentUser.email ?: ""
                
                tvUserName.text = displayName
                tvUserEmail.text = displayEmail
                etName.setText(displayName)
                etEmail.setText(displayEmail)
            } else {
                // fallback to Firebase auth data
                val displayName = currentUser.displayName ?: ""
                val displayEmail = currentUser.email ?: ""
                
                tvUserName.text = displayName
                tvUserEmail.text = displayEmail
                etName.setText(displayName)
                etEmail.setText(displayEmail)
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

    private fun setupObservers() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            // Disable buttons during loading
            btnEdit.isEnabled = !isLoading
            
            // Show loading text
            if (isLoading && isEditMode) {
                    btnEdit.text = "Menyimpan..."
                } else if (!isEditMode) {
                    btnEdit.text = "Edit"
            }
        })

        viewModel.updateSuccess.observe(this, Observer { success ->
            if (success) {
                android.widget.Toast.makeText(this, "Profil berhasil diperbarui", android.widget.Toast.LENGTH_SHORT).show()
                setEditMode(false)
                viewModel.clearMessages()
            }
        })


        viewModel.error.observe(this, Observer { error ->
            if (error != null) {
                val message = when {
                    error.message?.contains("network") == true -> "Periksa koneksi internet Anda"
                    error.message?.contains("too-many-requests") == true -> "Terlalu banyak permintaan. Coba lagi nanti."
                    error.message?.contains("requires-recent-login") == true -> "Silakan login ulang untuk melakukan perubahan ini"
                    else -> "Terjadi kesalahan: ${error.message}"
                }
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
                Log.w("ProfileActivity", "Error: ${error.message}")
                viewModel.clearMessages()
            }
        })
    }

    private fun setEditMode(enabled: Boolean) {
        isEditMode = enabled
        etName.isEnabled = enabled
        // Email is always disabled - cannot be edited
        etEmail.isEnabled = false
        
        if (enabled) {
            btnEdit.text = "Simpan"
            etName.requestFocus()
            
            // Show keyboard
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(etName, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        } else {
            btnEdit.text = "Edit"
            
            // Clear focus and hide keyboard
            etName.clearFocus()
            etEmail.clearFocus()
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    private fun setupClickListeners() {

        // Edit button
        findViewById<TextView>(R.id.btn_edit)?.setOnClickListener {
            if (isEditMode) {
                // Save changes
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                
                if (name.isEmpty()) {
                    etName.error = "Nama tidak boleh kosong"
                    return@setOnClickListener
                }
                
                viewModel.updateProfile(name, email)
            } else {
                // Enter edit mode
                setEditMode(true)
            }
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

    override fun onBackPressed() {
        if (isEditMode) {
            // If in edit mode, exit edit mode instead of going back
            AlertDialog.Builder(this)
                .setTitle("Batalkan Edit")
                .setMessage("Perubahan yang belum disimpan akan hilang. Yakin ingin membatalkan?")
                .setPositiveButton("Ya") { _, _ ->
                    setEditMode(false)
                    // Reload original data
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        viewModel.loadUser(currentUser.uid)
                    }
                }
                .setNegativeButton("Tidak", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }

    // Navigation handled by BottomNavigationHandler
}