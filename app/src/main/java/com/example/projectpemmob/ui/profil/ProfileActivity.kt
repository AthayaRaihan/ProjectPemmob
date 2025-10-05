package com.example.projectpemmob.ui.profil

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpemmob.MainActivity
import com.example.projectpemmob.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.wisata.WisataActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var auth: FirebaseAuth
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Initialize views
        initViews()

        // Setup data
        setupUserData()

        // Setup bottom navigation
        setupBottomNavigation()

        // Setup click listeners
        setupClickListeners()
        
        // Add debug info
        addDebugInfo()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh user data setiap kali activity resume
        android.util.Log.d("ProfileActivity", "Activity resumed, refreshing user data")
        setupUserData()
    }

    private fun initViews() {
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        tvUserName = findViewById(R.id.tv_user_name)
        tvUserEmail = findViewById(R.id.tv_user_email)
        
        // Set initial loading state
        tvUserName.text = "Loading..."
        tvUserEmail.text = "Loading..."
        etName.hint = "Loading..."
        etEmail.hint = "Loading..."
    }

    private fun setupUserData() {
        android.util.Log.d("ProfileActivity", "Setting up user data...")
        
        val currentUser = auth.currentUser
        if (currentUser != null) {
            android.util.Log.d("ProfileActivity", "Current user found: ${currentUser.uid}")
            android.util.Log.d("ProfileActivity", "Initial - Name: '${currentUser.displayName}', Email: '${currentUser.email}'")
            
            // Coba load langsung dulu
            loadUserProfile()
            
            // Reload user untuk memastikan data terbaru
            currentUser.reload().addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    android.util.Log.d("ProfileActivity", "User reloaded successfully")
                    
                    // Load ulang setelah reload
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadUserProfile()
                    }, 200)
                    
                    // Load ulang lagi dengan delay lebih lama untuk memastikan
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadUserProfile()
                    }, 1000)
                } else {
                    android.util.Log.w("ProfileActivity", "Failed to reload user", reloadTask.exception)
                    // Tetap coba load dengan delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadUserProfile()
                    }, 500)
                }
            }
        } else {
            android.util.Log.w("ProfileActivity", "No current user found")
            // Redirect ke login jika tidak ada user
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            redirectToLogin()
        }
    }
    
    private fun loadUserProfile() {
        android.util.Log.d("ProfileActivity", "Loading user profile...")
        
        val currentUser = auth.currentUser
        if (currentUser != null) {
            android.util.Log.d("ProfileActivity", "Raw user data:")
            android.util.Log.d("ProfileActivity", "- UID: ${currentUser.uid}")
            android.util.Log.d("ProfileActivity", "- Email: '${currentUser.email}'")
            android.util.Log.d("ProfileActivity", "- DisplayName: '${currentUser.displayName}'")
            android.util.Log.d("ProfileActivity", "- IsEmailVerified: ${currentUser.isEmailVerified}")
            
            // Get display name with better fallback
            val userName = when {
                !currentUser.displayName.isNullOrBlank() -> {
                    android.util.Log.d("ProfileActivity", "Using displayName: ${currentUser.displayName}")
                    currentUser.displayName!!
                }
                !currentUser.email.isNullOrBlank() -> {
                    // Extract name from email if display name is empty
                    val emailPart = currentUser.email!!.substringBefore("@")
                    val extractedName = emailPart.split(".", "_").joinToString(" ") { 
                        it.replaceFirstChar { char -> char.uppercase() } 
                    }
                    android.util.Log.d("ProfileActivity", "Extracted name from email: $extractedName")
                    extractedName
                }
                else -> {
                    android.util.Log.d("ProfileActivity", "Using fallback: User")
                    "User"
                }
            }
            
            val userEmail = currentUser.email ?: "user@example.com"
            
            android.util.Log.d("ProfileActivity", "Final - Name: '$userName', Email: '$userEmail'")
            
            // Update UI in main thread
            runOnUiThread {
                try {
                    // Update dengan data yang sudah diproses
                    tvUserName.text = userName
                    tvUserEmail.text = userEmail
                    etName.setText(userName)
                    etEmail.setText(userEmail)
                    
                    // Update hints juga
                    etName.hint = "Masukkan nama Anda"
                    etEmail.hint = "Email tidak dapat diubah"
                    
                    // Disable edit fields initially
                    etName.isEnabled = false
                    etEmail.isEnabled = false
                    
                    android.util.Log.d("ProfileActivity", "UI updated successfully")
                    
                    // Force refresh UI
                    tvUserName.invalidate()
                    tvUserEmail.invalidate()
                    etName.invalidate()
                    etEmail.invalidate()
                } catch (e: Exception) {
                    android.util.Log.e("ProfileActivity", "Error updating UI", e)
                    // Set error state
                    tvUserName.text = "Error loading name"
                    tvUserEmail.text = "Error loading email"
                }
            }
        } else {
            android.util.Log.w("ProfileActivity", "Current user is null when loading profile")
            
            // Set error state in UI
            runOnUiThread {
                tvUserName.text = "User tidak ditemukan"
                tvUserEmail.text = "Silakan login kembali"
                etName.setText("")
                etEmail.setText("")
                etName.hint = "Login diperlukan"
                etEmail.hint = "Login diperlukan"
            }
            
            // Redirect to login after showing error
            Handler(Looper.getMainLooper()).postDelayed({
                redirectToLogin()
            }, 2000)
        }
    }

    private fun setupClickListeners() {
        android.util.Log.d("ProfileActivity", "Setting up click listeners...")
        
        // Reset Password button
        val btnResetPassword = findViewById<TextView>(R.id.btn_reset_password)
        if (btnResetPassword != null) {
            btnResetPassword.setOnClickListener {
                android.util.Log.d("ProfileActivity", "Reset password button clicked")
                resetPassword()
            }
            android.util.Log.d("ProfileActivity", "Reset password button listener set")
        } else {
            android.util.Log.w("ProfileActivity", "Reset password button not found")
        }

        // Edit button
        val btnEdit = findViewById<TextView>(R.id.btn_edit)
        if (btnEdit != null) {
            btnEdit.setOnClickListener {
                android.util.Log.d("ProfileActivity", "Edit button clicked")
                toggleEditMode()
            }
            android.util.Log.d("ProfileActivity", "Edit button listener set")
        } else {
            android.util.Log.w("ProfileActivity", "Edit button not found")
        }

        // Destinasi Favorit
        val llDestinasiFavorit = findViewById<LinearLayout>(R.id.ll_destinasi_favorit)
        if (llDestinasiFavorit != null) {
            llDestinasiFavorit.setOnClickListener {
                android.util.Log.d("ProfileActivity", "Destinasi favorit clicked")
                val intent = Intent(this, FavoritActivity::class.java)
                startActivity(intent)
            }
            android.util.Log.d("ProfileActivity", "Destinasi favorit listener set")
        } else {
            android.util.Log.w("ProfileActivity", "Destinasi favorit layout not found")
        }

        // Keluar (Logout) button
        val btnKeluar = findViewById<TextView>(R.id.btn_keluar)
        if (btnKeluar != null) {
            btnKeluar.setOnClickListener {
                android.util.Log.d("ProfileActivity", "Logout button clicked")
                performLogout()
            }
            android.util.Log.d("ProfileActivity", "Logout button listener set")
        } else {
            android.util.Log.w("ProfileActivity", "Logout button not found")
        }
        
        android.util.Log.d("ProfileActivity", "All click listeners setup completed")
    }

    private fun setupBottomNavigation() {
        try {
            // Icon Home untuk kembali ke homepage
            findViewById<LinearLayout>(R.id.ll_home)?.setOnClickListener {
                val intent = Intent(this, HomepageActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Icon Location untuk ke halaman wisata
            findViewById<LinearLayout>(R.id.ll_location)?.setOnClickListener {
                val intent = Intent(this, WisataActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Icon Favorites untuk ke halaman favorit
            findViewById<LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                val intent = Intent(this, FavoritActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Icon Restaurant untuk ke halaman kuliner
            findViewById<LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                val intent = Intent(this, KulinerActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Icon Profile - sudah di halaman profile, tidak perlu action
            findViewById<LinearLayout>(R.id.ll_profile)?.setOnClickListener {
                // Sudah di halaman profile, tidak perlu navigasi
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun toggleEditMode() {
        android.util.Log.d("ProfileActivity", "Toggle edit mode - current isEditMode: $isEditMode")
        
        isEditMode = !isEditMode
        
        val btnEdit = findViewById<TextView>(R.id.btn_edit)
        
        if (isEditMode) {
            // Enable editing
            android.util.Log.d("ProfileActivity", "Enabling edit mode")
            etName.isEnabled = true
            etEmail.isEnabled = false // Email tidak bisa diedit
            btnEdit?.text = "Simpan"
            etName.requestFocus()
            
            // Show toast untuk user guidance
            Toast.makeText(this, "Mode edit aktif - ubah nama Anda", Toast.LENGTH_SHORT).show()
            
            android.util.Log.d("ProfileActivity", "Edit mode enabled")
        } else {
            // Save changes
            android.util.Log.d("ProfileActivity", "Saving changes")
            saveProfileChanges()
        }
    }
    
    private fun saveProfileChanges() {
        android.util.Log.d("ProfileActivity", "=== SAVING PROFILE CHANGES ===")
        
        val newName = etName.text.toString().trim()
        android.util.Log.d("ProfileActivity", "New name entered: '$newName'")
        
        if (newName.isEmpty()) {
            android.util.Log.w("ProfileActivity", "Name is empty")
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        
        val currentUser = auth.currentUser
        if (currentUser != null) {
            android.util.Log.d("ProfileActivity", "Updating profile for user: ${currentUser.uid}")
            
            // Show loading
            val btnEdit = findViewById<TextView>(R.id.btn_edit)
            btnEdit?.text = "Menyimpan..."
            etName.isEnabled = false
            
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            
            currentUser.updateProfile(profileUpdates).addOnCompleteListener { task ->
                runOnUiThread {
                    if (task.isSuccessful) {
                        android.util.Log.d("ProfileActivity", "Profile update successful")
                        
                        // Update UI
                        tvUserName.text = newName
                        etName.isEnabled = false
                        btnEdit?.text = "Edit"
                        isEditMode = false
                        
                        Toast.makeText(this, "Profile berhasil diupdate!", Toast.LENGTH_SHORT).show()
                        
                        // Reload user untuk sync
                        currentUser.reload().addOnCompleteListener {
                            android.util.Log.d("ProfileActivity", "User reloaded after profile update")
                        }
                        
                    } else {
                        android.util.Log.w("ProfileActivity", "Profile update failed", task.exception)
                        Toast.makeText(this, "Gagal mengupdate profile: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        
                        // Reset UI
                        btnEdit?.text = "Edit"
                        etName.isEnabled = false
                        isEditMode = false
                    }
                }
            }
        } else {
            android.util.Log.w("ProfileActivity", "No current user found when saving")
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun resetPassword() {
        android.util.Log.d("ProfileActivity", "=== RESET PASSWORD CLICKED ===")
        
        val currentUser = auth.currentUser
        val email = currentUser?.email
        
        android.util.Log.d("ProfileActivity", "Current user email: $email")
        
        if (email != null && email.isNotEmpty()) {
            android.util.Log.d("ProfileActivity", "Sending password reset email to: $email")
            
            // Show loading state
            val btnResetPassword = findViewById<TextView>(R.id.btn_reset_password)
            btnResetPassword?.text = "Mengirim..."
            
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    runOnUiThread {
                        btnResetPassword?.text = "Reset Password"
                        
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email reset password telah dikirim ke $email\\nSilakan cek inbox Anda", Toast.LENGTH_LONG).show()
                            android.util.Log.d("ProfileActivity", "Password reset email sent successfully")
                        } else {
                            val errorMsg = task.exception?.message ?: "Unknown error"
                            Toast.makeText(this, "Gagal mengirim email reset password: $errorMsg", Toast.LENGTH_LONG).show()
                            android.util.Log.w("ProfileActivity", "Failed to send password reset email", task.exception)
                        }
                    }
                }
        } else {
            android.util.Log.w("ProfileActivity", "Email is null or empty")
            Toast.makeText(this, "Email tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun performLogout() {
        android.util.Log.d("ProfileActivity", "=== PERFORMING LOGOUT ===")
        
        try {
            val currentUser = auth.currentUser
            android.util.Log.d("ProfileActivity", "Current user before logout: ${currentUser?.email}")
            
            // Sign out from Firebase
            auth.signOut()
            
            // Verify logout
            val userAfterLogout = auth.currentUser
            android.util.Log.d("ProfileActivity", "User after logout: $userAfterLogout")
            
            if (userAfterLogout == null) {
                android.util.Log.d("ProfileActivity", "Logout successful - user is null")
            } else {
                android.util.Log.w("ProfileActivity", "Logout may have failed - user still exists")
            }
            
            android.util.Log.d("ProfileActivity", "Firebase Auth sign out completed")
            
            // Show toast message
            Toast.makeText(this, "Berhasil logout! Mengarahkan ke login...", Toast.LENGTH_LONG).show()
            
            // Clear activity stack and redirect
            Handler(Looper.getMainLooper()).postDelayed({
                android.util.Log.d("ProfileActivity", "Redirecting to MainActivity")
                
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                
                // Force finish this activity
                finish()
                finishAffinity()
                
            }, 1000)
            
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "Error during logout", e)
            Toast.makeText(this, "Error logout: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun redirectToLogin() {
        android.util.Log.d("ProfileActivity", "Redirecting to login screen")
        
        val intent = Intent(this, com.example.projectpemmob.ui.auth.LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun addDebugInfo() {
        android.util.Log.d("ProfileActivity", "=== DEBUG INFO ===")
        android.util.Log.d("ProfileActivity", "btn_reset_password exists: ${findViewById<TextView>(R.id.btn_reset_password) != null}")
        android.util.Log.d("ProfileActivity", "btn_edit exists: ${findViewById<TextView>(R.id.btn_edit) != null}")
        android.util.Log.d("ProfileActivity", "btn_keluar exists: ${findViewById<TextView>(R.id.btn_keluar) != null}")
        android.util.Log.d("ProfileActivity", "ll_destinasi_favorit exists: ${findViewById<LinearLayout>(R.id.ll_destinasi_favorit) != null}")
        
        val currentUser = auth.currentUser
        android.util.Log.d("ProfileActivity", "Firebase Auth currentUser: ${currentUser?.email}")
        android.util.Log.d("ProfileActivity", "==================")
    }
}