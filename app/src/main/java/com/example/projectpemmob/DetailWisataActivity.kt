package com.example.projectpemmob

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailWisataActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_wisata_main)
        
        // Get data from intent
        val namaWisata = intent.getStringExtra("nama_wisata") ?: "Dieng Plateau"
        val rating = intent.getStringExtra("rating") ?: "4.8"
        val lokasi = intent.getStringExtra("lokasi") ?: "Kalimanah, Wonosobo, Jawa Tengah"
        
        // Setup views
        setupViews(namaWisata, rating, lokasi)
        
        // Setup back button
        setupBackButton()
        
        // Setup bottom navigation
        setupBottomNavigation()
    }
    
    private fun setupViews(namaWisata: String, rating: String, lokasi: String) {
        findViewById<TextView>(R.id.tv_nama_wisata).text = namaWisata
        findViewById<TextView>(R.id.tv_rating).text = rating
        findViewById<TextView>(R.id.tv_lokasi).text = lokasi
    }
    
    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }
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
}