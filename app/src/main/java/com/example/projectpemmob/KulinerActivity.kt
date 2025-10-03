package com.example.projectpemmob

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class KulinerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuliner_main)
        
        // Setup bottom navigation
        setupBottomNavigation()
        
        // Setup card click listeners
        setupCardClickListeners()
        
        // Setup heart icon listeners
        setupHeartIconListeners()
    }
    
    override fun onResume() {
        super.onResume()
        // Update heart icon states when returning to this activity
        updateAllHeartIcons()
    }

    private fun setupBottomNavigation() {
        try {
            // Icon Home untuk kembali ke homepage
            findViewById<android.widget.LinearLayout>(R.id.ll_home)?.setOnClickListener {
                val intent = Intent(this, HomepageActivity::class.java)
                startActivity(intent)
                finish() // Tutup activity kuliner
            }
            
            // Icon Location untuk ke halaman wisata
            findViewById<android.widget.LinearLayout>(R.id.ll_location)?.setOnClickListener {
                val intent = Intent(this, WisataActivity::class.java)
                startActivity(intent)
            }
            
            // Icon Favorites untuk ke halaman favorit
            findViewById<android.widget.LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                val intent = Intent(this, FavoritActivity::class.java)
                startActivity(intent)
            }
            
            // Icon Restaurant - sudah di halaman kuliner, tidak perlu action
            findViewById<android.widget.LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                // Sudah di halaman kuliner, tidak perlu navigasi
            }
            
            // Icon Profile - placeholder untuk fitur masa depan
            findViewById<android.widget.LinearLayout>(R.id.ll_profile)?.setOnClickListener {
                // Placeholder untuk halaman profile (belum dibuat)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupCardClickListeners() {
        // Card Kuliner click listeners 
        findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok)?.setOnClickListener {
            openDetailKuliner("Mie Ongklok Abang Adek", "4.6", "Wonosobo, Jawa Tengah")
        }
        
        findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok_2)?.setOnClickListener {
            openDetailKuliner("Mie Ongklok Ibu Bapak", "4.5", "Wonosobo, Jawa Tengah")
        }
        
        findViewById<androidx.cardview.widget.CardView>(R.id.card_dawet)?.setOnClickListener {
            openDetailKuliner("Es Dawet Ayu", "4.7", "Wonosobo, Jawa Tengah")
        }
        
        findViewById<androidx.cardview.widget.CardView>(R.id.card_sate_buntel)?.setOnClickListener {
            openDetailKuliner("Sate Buntel Khas", "4.4", "Wonosobo, Jawa Tengah")
        }
    }
    
    private fun openDetailKuliner(namaKuliner: String, rating: String, lokasi: String) {
        val intent = Intent(this, DetailKulinerActivity::class.java)
        intent.putExtra("nama_kuliner", namaKuliner)
        intent.putExtra("rating", rating)
        intent.putExtra("lokasi", lokasi)
        startActivity(intent)
    }
    
    private fun setupHeartIconListeners() {
        // Heart icon listeners untuk setiap card kuliner
        setupHeartIcon(R.id.heart_mie_ongklok, "Mie Ongklok Abang Adek", "4.6", "Wonosobo, Jawa Tengah")
        setupHeartIcon(R.id.heart_mie_ongklok_2, "Mie Ongklok Ibu Bapak", "4.5", "Wonosobo, Jawa Tengah")
        setupHeartIcon(R.id.heart_dawet, "Es Dawet Ayu", "4.7", "Wonosobo, Jawa Tengah")
        setupHeartIcon(R.id.heart_sate_buntel, "Sate Buntel Khas", "4.4", "Wonosobo, Jawa Tengah")
    }
    
    private fun setupHeartIcon(heartIconId: Int, namaKuliner: String, rating: String, lokasi: String) {
        val heartIcon = findViewById<ImageView>(heartIconId)
        
        if (heartIcon != null) {
            // Set initial state
            updateHeartIcon(heartIcon, namaKuliner, rating, lokasi)
            
            // Set click listener
            heartIcon.setOnClickListener {
                toggleFavorit(heartIcon, namaKuliner, rating, lokasi)
            }
        }
    }
    
    private fun updateHeartIcon(heartIcon: ImageView, namaKuliner: String, rating: String, lokasi: String) {
        if (FavoritManager.isFavorit(this, namaKuliner, rating, lokasi)) {
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        } else {
            heartIcon.setImageResource(R.drawable.ic_heart)
        }
    }
    
    private fun toggleFavorit(heartIcon: ImageView, namaKuliner: String, rating: String, lokasi: String) {
        if (FavoritManager.isFavorit(this, namaKuliner, rating, lokasi)) {
            // Remove from favorit
            FavoritManager.removeFromFavorit(this, namaKuliner, rating, lokasi)
            heartIcon.setImageResource(R.drawable.ic_heart)
        } else {
            // Add to favorit
            FavoritManager.addToFavorit(this, namaKuliner, rating, lokasi)
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        }
    }
    
    private fun updateAllHeartIcons() {
        findViewById<ImageView>(R.id.heart_mie_ongklok)?.let { heartIcon ->
            updateHeartIcon(heartIcon, "Mie Ongklok Abang Adek", "4.6", "Wonosobo, Jawa Tengah")
        }
        findViewById<ImageView>(R.id.heart_mie_ongklok_2)?.let { heartIcon ->
            updateHeartIcon(heartIcon, "Mie Ongklok Ibu Bapak", "4.5", "Wonosobo, Jawa Tengah")
        }
        findViewById<ImageView>(R.id.heart_dawet)?.let { heartIcon ->
            updateHeartIcon(heartIcon, "Es Dawet Ayu", "4.7", "Wonosobo, Jawa Tengah")
        }
        findViewById<ImageView>(R.id.heart_sate_buntel)?.let { heartIcon ->
            updateHeartIcon(heartIcon, "Sate Buntel Khas", "4.4", "Wonosobo, Jawa Tengah")
        }
    }
}