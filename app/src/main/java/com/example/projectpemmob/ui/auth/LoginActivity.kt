package com.example.projectpemmob.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.R
import com.example.projectpemmob.ui.auth.RegisterActivity
import com.example.projectpemmob.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var isPasswordVisible = false
    
    // Modern ActivityResultLauncher untuk Google Sign-in
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        android.util.Log.d("LoginActivity", "=== GOOGLE SIGN-IN RESULT RECEIVED ===")
        android.util.Log.d("LoginActivity", "Result code: ${result.resultCode}")
        android.util.Log.d("LoginActivity", "RESULT_OK: $RESULT_OK")
        android.util.Log.d("LoginActivity", "RESULT_CANCELED: $RESULT_CANCELED")
        
        // Reset button state
        resetGoogleSignInButton()
        
        when (result.resultCode) {
            RESULT_OK -> {
                android.util.Log.d("LoginActivity", "Google Sign-in result: OK")
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
            RESULT_CANCELED -> {
                android.util.Log.w("LoginActivity", "Google Sign-in was cancelled by user")
                Toast.makeText(this, "Google Sign-in dibatalkan", Toast.LENGTH_SHORT).show()
            }
            else -> {
                android.util.Log.w("LoginActivity", "Google Sign-in failed with result code: ${result.resultCode}")
                Toast.makeText(this, "Google Sign-in gagal (Code: ${result.resultCode})", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In dengan error handling
        try {
            // Verifikasi bahwa Web Client ID sudah dikonfigurasi dengan benar
            val webClientId = getString(R.string.default_web_client_id)
            android.util.Log.d("LoginActivity", "Web Client ID: $webClientId")
            
            if (webClientId.isEmpty() || webClientId.contains("your_web_client_id_here")) {
                throw Exception("Web Client ID belum dikonfigurasi dengan benar di strings.xml")
            }
            
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .requestProfile()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            
            android.util.Log.d("LoginActivity", "Google Sign-in configured successfully")
            
            // Test apakah Google Play Services tersedia
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
            
            if (resultCode == ConnectionResult.SUCCESS) {
                android.util.Log.d("LoginActivity", "Google Play Services available")
            } else {
                android.util.Log.w("LoginActivity", "Google Play Services issue: $resultCode")
                val errorMessage = when (resultCode) {
                    ConnectionResult.SERVICE_MISSING -> "Google Play Services tidak terinstall"
                    ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> "Google Play Services perlu diupdate"
                    ConnectionResult.SERVICE_DISABLED -> "Google Play Services dinonaktifkan"
                    else -> "Google Play Services bermasalah (Code: $resultCode)"
                }
                android.util.Log.w("LoginActivity", errorMessage)
            }
            
        } catch (e: Exception) {
            android.util.Log.e("LoginActivity", "Error configuring Google Sign-in: ${e.message}")
            Toast.makeText(this, "Error konfigurasi Google Sign-in: ${e.message}", Toast.LENGTH_LONG).show()
            
            // Disable Google Sign-in button jika konfigurasi gagal
            binding.btnGoogleSignIn.isEnabled = false
            binding.btnGoogleSignIn.alpha = 0.5f
        }

        // Setup Google Sign In button
        binding.btnGoogleSignIn.setOnClickListener {
            android.util.Log.d("LoginActivity", "=== GOOGLE SIGN-IN BUTTON CLICKED ===")
            
            // Check if button is enabled
            if (!binding.btnGoogleSignIn.isEnabled) {
                android.util.Log.w("LoginActivity", "Google Sign-in button is disabled")
                Toast.makeText(this, "Google Sign-in tidak tersedia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Check current user
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                android.util.Log.d("LoginActivity", "User already signed in: ${currentUser.email}")
                Toast.makeText(this, "Anda sudah login sebagai ${currentUser.email}", Toast.LENGTH_SHORT).show()
                // Navigate to homepage
                val intent = Intent(this, HomepageActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }
            
            // Check network connectivity
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            try {
                signInWithGoogle()
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error starting Google Sign-in: ${e.message}")
                Toast.makeText(this, "Error memulai Google Sign-in: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            android.util.Log.d("LoginActivity", "=== LOGIN SUCCESSFUL ===")
                            android.util.Log.d("LoginActivity", "Email: $email")
                            
                            val user = firebaseAuth.currentUser
                            android.util.Log.d("LoginActivity", "User UID: ${user?.uid}")
                            android.util.Log.d("LoginActivity", "Initial DisplayName: '${user?.displayName}'")
                            android.util.Log.d("LoginActivity", "Email: '${user?.email}'")
                            
                            // Tunggu lebih lama untuk memastikan profile ter-load
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                // Reload user untuk memastikan profile terbaru
                                user?.reload()?.addOnCompleteListener { reloadTask ->
                                    if (reloadTask.isSuccessful) {
                                        val refreshedUser = firebaseAuth.currentUser
                                        android.util.Log.d("LoginActivity", "After reload - DisplayName: '${refreshedUser?.displayName}'")
                                        android.util.Log.d("LoginActivity", "After reload - Email: '${refreshedUser?.email}'")
                                    } else {
                                        android.util.Log.e("LoginActivity", "User reload failed: ${reloadTask.exception?.message}")
                                    }
                                    
                                    // Tambah delay tambahan sebelum pindah ke homepage
                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                        android.util.Log.d("LoginActivity", "Navigating to homepage...")
                                        val intent = Intent(this, HomepageActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }, 500) // Delay tambahan 500ms
                                } ?: run {
                                    android.util.Log.d("LoginActivity", "User reload failed or null")
                                    val intent = Intent(this, HomepageActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }, 1000) // Delay 1 detik
                        } else {
                            android.util.Log.e("LoginActivity", "Login failed: ${it.exception?.message}")
                            Toast.makeText(this, "Login gagal: ${it.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Field Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Ambil view dari XML
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // Arahkan ke RegisterActivity kalau diklik
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.ivTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                // sembunyikan password
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_closed)
                isPasswordVisible = false
            } else {
                // tampilkan password
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_open)
                isPasswordVisible = true
            }
            binding.etPassword.setSelection(binding.etPassword.text.length) // cursor tetap di akhir
        }
    }

    private fun signInWithGoogle() {
        android.util.Log.d("LoginActivity", "=== STARTING GOOGLE SIGN-IN PROCESS ===")
        
        try {
            // Check network connectivity
            val connectivityManager = getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            val isConnected = activeNetwork?.isConnectedOrConnecting == true
            
            android.util.Log.d("LoginActivity", "Network connected: $isConnected")
            
            if (!isConnected) {
                Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_LONG).show()
                return
            }
            
            // Disable button temporarily to prevent multiple clicks
            binding.btnGoogleSignIn.isEnabled = false
            binding.btnGoogleSignIn.text = "Connecting..."
            
            // Sign out any previous session to force account selection
            googleSignInClient.signOut().addOnCompleteListener { signOutTask ->
                android.util.Log.d("LoginActivity", "Previous session signed out: ${signOutTask.isSuccessful}")
                
                // Start the sign-in intent using modern ActivityResultLauncher
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
                
                android.util.Log.d("LoginActivity", "Google Sign-in intent launched")
                
                // Re-enable button after a delay
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    binding.btnGoogleSignIn.isEnabled = true
                    binding.btnGoogleSignIn.text = "Sign in with Google"
                }, 3000) // 3 seconds
            }
            
        } catch (e: Exception) {
            android.util.Log.e("LoginActivity", "Error in signInWithGoogle: ${e.message}")
            Toast.makeText(this, "Error Google Sign-in: ${e.message}", Toast.LENGTH_LONG).show()
            
            // Re-enable button on error
            binding.btnGoogleSignIn.isEnabled = true
            binding.btnGoogleSignIn.text = "Sign in with Google"
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            android.util.Log.d("LoginActivity", "Handling Google Sign-in result")
            
            val account = completedTask.getResult(ApiException::class.java)
            
            android.util.Log.d("LoginActivity", "Google account received:")
            android.util.Log.d("LoginActivity", "- Display Name: ${account?.displayName}")
            android.util.Log.d("LoginActivity", "- Email: ${account?.email}")
            android.util.Log.d("LoginActivity", "- ID Token: ${if (account?.idToken != null) "Present" else "NULL"}")
            
            if (account?.idToken != null) {
                // Signed in successfully
                firebaseAuthWithGoogle(account.idToken!!)
            } else {
                android.util.Log.e("LoginActivity", "ID Token is null")
                Toast.makeText(this, "Gagal mendapatkan ID Token dari Google", Toast.LENGTH_LONG).show()
            }
        } catch (e: ApiException) {
            // Sign in failed
            android.util.Log.e("LoginActivity", "Google Sign-in failed - Status Code: ${e.statusCode}")
            android.util.Log.e("LoginActivity", "Google Sign-in failed - Message: ${e.message}")
            
            val errorMessage = when (e.statusCode) {
                12501 -> "Google Sign-in dibatalkan oleh user"
                12502 -> "Google Sign-in gagal - Network error"
                12500 -> "Google Sign-in gagal - Internal error"
                else -> "Gagal masuk dengan Google: ${e.message} (Code: ${e.statusCode})"
            }
            
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            android.util.Log.e("LoginActivity", "Unexpected error in handleSignInResult: ${e.message}")
            Toast.makeText(this, "Error tidak terduga: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        android.util.Log.d("LoginActivity", "=== FIREBASE AUTH WITH GOOGLE ===")
        android.util.Log.d("LoginActivity", "ID Token received: ${idToken.take(20)}...")
        
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            android.util.Log.d("LoginActivity", "Google credential created successfully")
            
            // Show progress to user
            binding.btnGoogleSignIn.text = "Authenticating..."
            
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    runOnUiThread {
                        binding.btnGoogleSignIn.text = "Sign in with Google"
                        
                        if (task.isSuccessful) {
                            android.util.Log.d("LoginActivity", "Firebase auth with Google successful")
                            val user = firebaseAuth.currentUser
                            
                            android.util.Log.d("LoginActivity", "Google user info:")
                            android.util.Log.d("LoginActivity", "- UID: ${user?.uid}")
                            android.util.Log.d("LoginActivity", "- DisplayName: '${user?.displayName}'")
                            android.util.Log.d("LoginActivity", "- Email: '${user?.email}'")
                            android.util.Log.d("LoginActivity", "- PhotoUrl: ${user?.photoUrl}")
                            android.util.Log.d("LoginActivity", "- IsEmailVerified: ${user?.isEmailVerified}")
                            
                            Toast.makeText(this, "Google Sign-in berhasil! Selamat datang ${user?.displayName ?: user?.email}", Toast.LENGTH_LONG).show()
                            
                            // Tunggu sebentar untuk memastikan data tersinkronisasi
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                android.util.Log.d("LoginActivity", "Navigating to HomepageActivity...")
                                val intent = Intent(this, HomepageActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }, 1000) // Delay 1 detik
                        } else {
                            // Sign in failed
                            val exception = task.exception
                            android.util.Log.e("LoginActivity", "Firebase auth with Google failed: ${exception?.message}")
                            android.util.Log.e("LoginActivity", "Exception type: ${exception?.javaClass?.simpleName}")
                            
                            val errorMessage = when {
                                exception?.message?.contains("network", ignoreCase = true) == true -> 
                                    "Gagal login Google: Masalah jaringan"
                                exception?.message?.contains("token", ignoreCase = true) == true -> 
                                    "Gagal login Google: Token tidak valid"
                                exception?.message?.contains("account", ignoreCase = true) == true -> 
                                    "Gagal login Google: Masalah akun"
                                else -> "Google Sign-in gagal: ${exception?.message}"
                            }
                            
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    runOnUiThread {
                        binding.btnGoogleSignIn.text = "Sign in with Google"
                        android.util.Log.e("LoginActivity", "Firebase signInWithCredential failed: ${exception.message}")
                        Toast.makeText(this, "Firebase auth gagal: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: Exception) {
            android.util.Log.e("LoginActivity", "Exception in firebaseAuthWithGoogle: ${e.message}")
            Toast.makeText(this, "Error Firebase auth: ${e.message}", Toast.LENGTH_LONG).show()
            binding.btnGoogleSignIn.text = "Sign in with Google"
        }
    }
    
    private fun resetGoogleSignInButton() {
        runOnUiThread {
            binding.btnGoogleSignIn.isEnabled = true
            binding.btnGoogleSignIn.text = "Sign in with Google"
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            networkCapabilities != null && (
                networkCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
            )
        } catch (e: Exception) {
            android.util.Log.w("LoginActivity", "Error checking network: ${e.message}")
            true // Assume network is available if check fails
        }
    }
}