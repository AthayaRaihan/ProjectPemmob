package com.example.projectpemmob.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpemmob.R
import com.example.projectpemmob.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            val confirmPass = binding.etConfirmPassword.text.toString()

            android.util.Log.d("RegisterActivity", "=== REGISTER ATTEMPT ===")
            android.util.Log.d("RegisterActivity", "Name: '$name'")
            android.util.Log.d("RegisterActivity", "Email: '$email'")

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && name.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                android.util.Log.d("RegisterActivity", "Account created successfully")
                                
                                val user = firebaseAuth.currentUser
                                android.util.Log.d("RegisterActivity", "User UID: ${user?.uid}")
                                
                                val profileUpdates = userProfileChangeRequest {
                                    displayName = name
                                }
                                
                                android.util.Log.d("RegisterActivity", "Updating profile with name: '$name'")
                                
                                user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        android.util.Log.d("RegisterActivity", "Profile updated successfully")
                                        
                                        // Reload user untuk memastikan profile tersimpan
                                        user.reload().addOnCompleteListener { reloadTask ->
                                            if (reloadTask.isSuccessful) {
                                                val refreshedUser = firebaseAuth.currentUser
                                                android.util.Log.d("RegisterActivity", "After reload - DisplayName: '${refreshedUser?.displayName}'")
                                            }
                                            
                                            Toast.makeText(this, "Register sukses dengan nama: $name", Toast.LENGTH_LONG).show()
                                            
                                            // Tambah delay sebelum pindah ke login
                                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                                val intent = Intent(this, LoginActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }, 1500) // Delay 1.5 detik
                                        }
                                    } else {
                                        android.util.Log.e("RegisterActivity", "Profile update failed: ${updateTask.exception?.message}")
                                        Toast.makeText(this, "Register sukses, tapi gagal update profil: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
                                        
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }?.addOnFailureListener { exception ->
                                    android.util.Log.e("RegisterActivity", "Profile update exception: ${exception.message}")
                                    Toast.makeText(this, "Gagal update profil: ${exception.message}", Toast.LENGTH_LONG).show()
                                    
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Field Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            }
        }

        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        // Arahkan ke LoginActivity kalau diklik
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // optional, supaya user tidak bisa kembali ke register dengan tombol back
        }

        binding.ivTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_closed)
                isPasswordVisible = false
            } else {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_open)
                isPasswordVisible = true
            }
            binding.etPassword.setSelection(binding.etPassword.text!!.length)
        }

        // === Toggle untuk Confirm Password ===
        binding.ivToggleConfirmPassword.setOnClickListener {
            if (isConfirmPasswordVisible) {
                binding.etConfirmPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_closed)
                isConfirmPasswordVisible = false
            } else {
                binding.etConfirmPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_open)
                isConfirmPasswordVisible = true
            }
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text!!.length)
        }

    }
}