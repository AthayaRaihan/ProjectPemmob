package com.example.projectpemmob.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpemmob.R
import com.example.projectpemmob.databinding.ActivityRegisterBinding
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

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

            // Basic validations
            if (name.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Masukkan email yang valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass.isEmpty() || pass.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmPass.isEmpty()) {
                Toast.makeText(this, "Konfirmasi password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != confirmPass) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user and write to Firestore immediately
            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser

                        // Start profile update asynchronously (do not block Firestore write)
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name
                        }
                        user?.updateProfile(profileUpdates)

                        // Prepare user data for Firestore
                        val db = FirebaseFirestore.getInstance()
                        val userData = hashMapOf<String, Any?>(
                            "name" to name,
                            "email" to email,
                            "photoUrl" to "", // default empty, user can set later in Profile
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                        // Use UID as document ID and attempt to write immediately
                        user?.uid?.let { uid ->
                            db.collection("users").document(uid).set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Register sukses dengan nama: $name", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Register sukses, namun gagal menyimpan data pengguna: ${e.message}", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        } ?: run {
                            // UID missing — fallback
                            Toast.makeText(this, "Register sukses, namun UID tidak tersedia.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
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