package com.example.projectpemmob

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.projectpemmob.R

class HomepageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)

        val tvGreetingName = findViewById<TextView>(R.id.tvGreetingName)

        // Ambil user yang login
        val user = FirebaseAuth.getInstance().currentUser

        val displayName = user?.displayName ?: "User" // fallback kalau null
        tvGreetingName.text = "Hai $displayName,"
    }
}
