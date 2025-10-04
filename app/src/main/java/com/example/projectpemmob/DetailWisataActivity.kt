package com.example.projectpemmob

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailWisataActivity : AppCompatActivity() {
    
    private var currentWisataData: HashMap<String, String>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_wisata)
        
        // Get data from intent
        val namaWisata = intent.getStringExtra("nama_wisata") ?: "Dieng Plateau"
        val rating = intent.getStringExtra("rating") ?: "4.8"
        val lokasi = intent.getStringExtra("lokasi") ?: "Banjarnegara, Jawa Tengah"
        val description = intent.getStringExtra("description") ?: "Dieng Plateau atau dataran tinggi Dieng, merupakan salah satu situs bersejarah paling terkenal di Jawa Tengah, Indonesia."
        
        // Store current wisata data for favorit functionality
        currentWisataData = hashMapOf(
            "nama_wisata" to namaWisata,
            "rating" to rating,
            "lokasi" to lokasi,
            "description" to description
        )
        
        // Setup views
        setupViews(namaWisata, rating, lokasi, description)
        
        // Setup buttons
        setupBackButton()
        setupFavoritButton()
        setupMapsButton()
        
        // Update favorit button state
        updateFavoritButtonState()
    }
    
    private fun setupViews(namaWisata: String, rating: String, lokasi: String, description: String) {
        findViewById<TextView>(R.id.tv_nama_wisata).text = namaWisata
        findViewById<TextView>(R.id.tv_rating).text = rating
        findViewById<TextView>(R.id.tv_location).text = lokasi
        findViewById<TextView>(R.id.tv_description).text = description
    }
    
    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }
    
    private fun setupFavoritButton() {
        findViewById<ImageView>(R.id.btn_favorite).setOnClickListener {
            currentWisataData?.let { data ->
                val namaWisata = data["nama_wisata"] ?: return@let
                
                if (FavoritManager.isFavorite(this, namaWisata)) {
                    // Remove from favorites
                    FavoritManager.removeFavorite(this, namaWisata)
                    Toast.makeText(this, "$namaWisata dihapus dari favorit", Toast.LENGTH_SHORT).show()
                } else {
                    // Add to favorites
                    FavoritManager.addFavorite(this, data)
                    Toast.makeText(this, "$namaWisata ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                }
                
                updateFavoritButtonState()
            }
        }
    }
    
    private fun setupMapsButton() {
        findViewById<ImageView>(R.id.btnOpenMaps).setOnClickListener {
            // Open location in Google Maps
            // Using Dieng Plateau coordinates as example
            val latitude = -7.2094 // Dieng Plateau latitude
            val longitude = 109.9036 // Dieng Plateau longitude
            val namaWisata = currentWisataData?.get("nama_wisata") ?: "Wisata"
            
            val uri = "geo:$latitude,$longitude?q=$latitude,$longitude($namaWisata)"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // If Google Maps is not installed, open in browser
                val browserUri = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(browserUri))
                startActivity(browserIntent)
            }
        }
    }
    
    private fun updateFavoritButtonState() {
        val btnFavorite = findViewById<ImageView>(R.id.btn_favorite)
        currentWisataData?.let { data ->
            val namaWisata = data["nama_wisata"] ?: return@let
            
            if (FavoritManager.isFavorite(this, namaWisata)) {
                btnFavorite.setImageResource(R.drawable.ic_heart_filled)
            } else {
                btnFavorite.setImageResource(R.drawable.ic_heart)
            }
        }
    }
}