package com.example.projectpemmob

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class WisataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wisata)
        
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
                finish() // Tutup activity wisata
            }
            
            // Icon Location - sudah di halaman wisata, tidak perlu action
            findViewById<android.widget.LinearLayout>(R.id.ll_location)?.setOnClickListener {
                // Sudah di halaman wisata, tidak perlu navigasi
            }
            
            // Icon Restaurant untuk ke halaman kuliner
            findViewById<android.widget.LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                val intent = Intent(this, KulinerActivity::class.java)
                startActivity(intent)
            }
            
            // Icon Favorites untuk ke halaman favorit
            findViewById<android.widget.LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                val intent = Intent(this, FavoritActivity::class.java)
                startActivity(intent)
            }
            
            // Icon Profile untuk navigasi ke halaman profile
            findViewById<android.widget.LinearLayout>(R.id.ll_profile)?.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupCardClickListeners() {
        // Card Wisata click listeners - Menggunakan ID yang akan ditambahkan ke layout
        findViewById<androidx.cardview.widget.CardView>(R.id.card_dieng_1).setOnClickListener {
            openDetailWisata("Dieng Plateau", "4.8", "Kalimanah, Wonosobo, Jawa Tengah")
        }

        
        findViewById<androidx.cardview.widget.CardView>(R.id.card_sikidang).setOnClickListener {
            openDetailWisata("Kawah Sikidang", "4.5", "Dieng, Wonosobo, Jawa Tengah")
        }
        
        findViewById<androidx.cardview.widget.CardView>(R.id.card_telaga_warna).setOnClickListener {
            openDetailWisata("Telaga Warna", "4.7", "Dieng, Wonosobo, Jawa Tengah")
        }
    }
    
    private fun openDetailWisata(namaWisata: String, rating: String, lokasi: String) {
        val intent = Intent(this, DetailWisataActivity::class.java)
        intent.putExtra("nama_wisata", namaWisata)
        intent.putExtra("rating", rating)
        intent.putExtra("lokasi", lokasi)
        startActivity(intent)
    }
    
    private fun setupHeartIconListeners() {
        // Heart icon listeners untuk setiap card wisata
        setupHeartIcon(R.id.heart_dieng_1, "Dieng Plateau", "4.8", "Kalimanah, Wonosobo, Jawa Tengah")

        setupHeartIcon(R.id.heart_sikidang, "Kawah Sikidang", "4.5", "Dieng, Wonosobo, Jawa Tengah")
        setupHeartIcon(R.id.heart_telaga_warna, "Telaga Warna", "4.7", "Dieng, Wonosobo, Jawa Tengah")
    }
    
    private fun setupHeartIcon(heartIconId: Int, namaWisata: String, rating: String, lokasi: String) {
        val heartIcon = findViewById<ImageView>(heartIconId)
        
        // Set initial state
        updateHeartIcon(heartIcon, namaWisata, rating, lokasi)
        
        // Set click listener
        heartIcon.setOnClickListener {
            toggleFavorit(heartIcon, namaWisata, rating, lokasi)
        }
    }
    
    private fun updateHeartIcon(heartIcon: ImageView, namaWisata: String, rating: String, lokasi: String) {
        if (FavoritManager.isFavorit(this, namaWisata, rating, lokasi)) {
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        } else {
            heartIcon.setImageResource(R.drawable.ic_heart)
        }
    }
    
    private fun toggleFavorit(heartIcon: ImageView, namaWisata: String, rating: String, lokasi: String) {
        if (FavoritManager.isFavorit(this, namaWisata, rating, lokasi)) {
            // Remove from favorit
            FavoritManager.removeFromFavorit(this, namaWisata, rating, lokasi)
            heartIcon.setImageResource(R.drawable.ic_heart)
        } else {
            // Add to favorit
            FavoritManager.addToFavorit(this, namaWisata, rating, lokasi)
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        }
    }
    
    private fun updateAllHeartIcons() {
        updateHeartIcon(findViewById(R.id.heart_dieng_1), "Dieng Plateau", "4.8", "Kalimanah, Wonosobo, Jawa Tengah")
        updateHeartIcon(findViewById(R.id.heart_sikidang), "Kawah Sikidang", "4.5", "Dieng, Wonosobo, Jawa Tengah")
        updateHeartIcon(findViewById(R.id.heart_telaga_warna), "Telaga Warna", "4.7", "Dieng, Wonosobo, Jawa Tengah")
    }
}