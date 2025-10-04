package com.example.projectpemmob

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailKulinerActivity : AppCompatActivity() {
    
    private var currentKulinerData: HashMap<String, String>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kuliner)
        
        // Get data from intent
        val namaKuliner = intent.getStringExtra("nama_kuliner") ?: "Gudeg Yu Djum"
        val rating = intent.getStringExtra("rating") ?: "4.5"
        val lokasi = intent.getStringExtra("lokasi") ?: "Yogyakarta, Jawa Tengah"
        val description = intent.getStringExtra("description") ?: "Gudeg Yu Djum adalah salah satu kuliner khas Yogyakarta yang sangat terkenal. Dengan cita rasa manis gurih yang khas, gudeg ini menggunakan resep turun temurun yang telah diwariskan sejak puluhan tahun."
        
        // Store current kuliner data
        currentKulinerData = hashMapOf(
            "nama_kuliner" to namaKuliner,
            "rating" to rating,
            "lokasi" to lokasi,
            "description" to description
        )
        
        // Setup views
        setupViews(namaKuliner, rating, lokasi, description)
        
        // Setup buttons
        setupBackButton()
        setupMapsButton()
    }
    
    private fun setupViews(namaKuliner: String, rating: String, lokasi: String, description: String) {
        findViewById<TextView>(R.id.tv_nama_kuliner).text = namaKuliner
        findViewById<TextView>(R.id.tv_rating).text = rating
        findViewById<TextView>(R.id.tv_location).text = lokasi
        findViewById<TextView>(R.id.tv_description).text = description
    }
    
    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }
    
    private fun setupMapsButton() {
        findViewById<ImageView>(R.id.btnOpenMaps).setOnClickListener {
            // Open location in Google Maps
            // Using Yogyakarta coordinates as example for culinary
            val latitude = -7.7956 // Yogyakarta latitude
            val longitude = 110.3695 // Yogyakarta longitude
            val namaKuliner = currentKulinerData?.get("nama_kuliner") ?: "Kuliner"
            
            val uri = "geo:$latitude,$longitude?q=$latitude,$longitude($namaKuliner)"
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
}