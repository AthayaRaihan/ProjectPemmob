package com.example.projectpemmob

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailKulinerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kuliner_main)
        
        // Get data from intent
        val namaKuliner = intent.getStringExtra("nama_kuliner") ?: "Kuliner Wonosobo"
        val rating = intent.getStringExtra("rating") ?: "4.5"
        val lokasi = intent.getStringExtra("lokasi") ?: "Wonosobo, Jawa Tengah"
        
        // Setup UI dengan data
        setupDetailKuliner(namaKuliner, rating, lokasi)
        
        // Setup bottom navigation
        setupBottomNavigation()
        
        // Setup heart icon
        setupHeartIcon(namaKuliner, rating, lokasi)
        
        // Setup back button
        setupBackButton()
    }
    
    override fun onResume() {
        super.onResume()
        // Update heart icon when returning to this activity
        val namaKuliner = intent.getStringExtra("nama_kuliner") ?: "Kuliner Wonosobo"
        val rating = intent.getStringExtra("rating") ?: "4.5"
        val lokasi = intent.getStringExtra("lokasi") ?: "Wonosobo, Jawa Tengah"
        updateHeartIcon(namaKuliner, rating, lokasi)
    }
    
    private fun setupDetailKuliner(namaKuliner: String, rating: String, lokasi: String) {
        // Set nama kuliner
        findViewById<TextView>(R.id.tv_nama_kuliner)?.text = namaKuliner
        
        // Set rating
        findViewById<TextView>(R.id.tv_rating_kuliner)?.text = rating
        
        // Set lokasi
        findViewById<TextView>(R.id.tv_lokasi_kuliner)?.text = lokasi
        
        // Set gambar berdasarkan nama kuliner
        val imageView = findViewById<ImageView>(R.id.img_kuliner_main)
        when {
            namaKuliner.contains("Mie Ongklok", ignoreCase = true) -> 
                imageView?.setImageResource(R.drawable.culinary_noodles)
            namaKuliner.contains("Dawet", ignoreCase = true) -> 
                imageView?.setImageResource(R.drawable.culinary_noodles)
            namaKuliner.contains("Sate", ignoreCase = true) -> 
                imageView?.setImageResource(R.drawable.culinary_1)
            else -> 
                imageView?.setImageResource(R.drawable.culinary_noodles)
        }
        
        // Set deskripsi berdasarkan nama kuliner
        val deskripsi = when {
            namaKuliner.contains("Mie Ongklok", ignoreCase = true) -> 
                "Mie Ongklok adalah kuliner khas Wonosobo yang terbuat dari mie tebal dengan kuah gurih dan sayuran segar. Kuliner ini sudah sangat terkenal di kalangan masyarakat Indonesia karena cita rasanya yang autentik dan bahan-bahan berkualitas tinggi dari dataran tinggi Dieng yang memberikan kesegaran dan kelezatan yang tak terlupakan."
            namaKuliner.contains("Dawet", ignoreCase = true) -> 
                "Es Dawet Ayu adalah minuman tradisional khas Wonosobo dengan cendol kenyal, santan segar, dan gula jawa yang manis. Minuman ini sangat cocok dinikmati di cuaca dingin pegunungan Dieng, memberikan kesegaran yang menyehatkan dan cita rasa manis yang autentik dari warisan kuliner Jawa Tengah."
            namaKuliner.contains("Sate Buntel", ignoreCase = true) -> 
                "Sate Buntel adalah kuliner unik khas Wonosobo dengan daging cincang yang dibungkus lemak dan dibakar hingga harum. Kuliner ini memiliki cita rasa yang khas dan berbeda dari sate pada umumnya, dengan bumbu kacang yang gurih dan pedas yang menggugah selera."
            else -> 
                "Kuliner khas Wonosobo dengan cita rasa yang autentik dan bahan-bahan berkualitas tinggi dari dataran tinggi Dieng. Nikmati kelezatan kuliner tradisional yang telah turun temurun dengan resep rahasia yang memberikan pengalaman kuliner yang tak terlupakan."
        }
        findViewById<TextView>(R.id.tv_deskripsi_kuliner)?.text = deskripsi
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
            
            // Icon Restaurant untuk ke halaman kuliner
            findViewById<android.widget.LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                val intent = Intent(this, KulinerActivity::class.java)
                startActivity(intent)
                finish()
            }
            
            // Icon Favorites untuk ke halaman favorit
            findViewById<android.widget.LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                val intent = Intent(this, FavoritActivity::class.java)
                startActivity(intent)
                finish()
            }
            
            // Icon Profile - placeholder untuk fitur masa depan
            findViewById<android.widget.LinearLayout>(R.id.ll_profile)?.setOnClickListener {
                // Placeholder untuk halaman profile (belum dibuat)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupHeartIcon(namaKuliner: String, rating: String, lokasi: String) {
        val heartIcon = findViewById<ImageView>(R.id.heart_detail_kuliner)
        
        if (heartIcon != null) {
            // Set initial state
            updateHeartIcon(namaKuliner, rating, lokasi)
            
            // Set click listener
            heartIcon.setOnClickListener {
                toggleFavorit(namaKuliner, rating, lokasi)
            }
        }
    }
    
    private fun updateHeartIcon(namaKuliner: String, rating: String, lokasi: String) {
        val heartIcon = findViewById<ImageView>(R.id.heart_detail_kuliner)
        if (heartIcon != null) {
            if (FavoritManager.isFavorit(this, namaKuliner, rating, lokasi)) {
                heartIcon.setImageResource(R.drawable.ic_heart_filled)
            } else {
                heartIcon.setImageResource(R.drawable.ic_heart)
            }
        }
    }
    
    private fun toggleFavorit(namaKuliner: String, rating: String, lokasi: String) {
        val heartIcon = findViewById<ImageView>(R.id.heart_detail_kuliner)
        if (heartIcon != null) {
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
    }
    
    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            finish() // Kembali ke activity sebelumnya
        }
    }
}