package com.example.projectpemmob

import android.content.Intent
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
        
        // Setup click listener untuk icon location (wisata)
        setupBottomNavigation()
        
        // Setup card click listeners
        setupCardClickListeners()
    }
    
    private fun setupBottomNavigation() {
        try {
            // Icon Location untuk navigasi ke halaman wisata
            findViewById<android.widget.LinearLayout>(R.id.ll_location)?.setOnClickListener {
                val intent = Intent(this, WisataActivity::class.java)
                startActivity(intent)
            }
            
            // Icon Restaurant untuk navigasi ke halaman kuliner
            findViewById<android.widget.LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                val intent = Intent(this, KulinerActivity::class.java)
                startActivity(intent)
            }
            
            // Icon Favorites untuk navigasi ke halaman favorit
            findViewById<android.widget.LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                val intent = Intent(this, FavoritActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupCardClickListeners() {
        try {
            // Card Wisata click listeners di homepage
            findViewById<androidx.cardview.widget.CardView>(R.id.card_homepage_dieng_1)?.setOnClickListener {
                openDetailWisata("Dieng Plateau", "4.8", "Kalimanah, Wonosobo, Jawa Tengah")
            }
            
            findViewById<androidx.cardview.widget.CardView>(R.id.card_homepage_dieng_2)?.setOnClickListener {
                openDetailWisata("Dieng Plateau", "4.8", "Kalimanah, Wonosobo, Jawa Tengah")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun openDetailWisata(namaWisata: String, rating: String, lokasi: String) {
        val intent = Intent(this, DetailWisataActivity::class.java)
        intent.putExtra("nama_wisata", namaWisata)
        intent.putExtra("rating", rating)
        intent.putExtra("lokasi", lokasi)
        startActivity(intent)
    }
}
