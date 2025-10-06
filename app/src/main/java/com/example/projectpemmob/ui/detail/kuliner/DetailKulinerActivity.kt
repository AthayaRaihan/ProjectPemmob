package com.example.projectpemmob.ui.detail.kuliner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Kuliner
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.wisata.WisataActivity
import com.example.projectpemmob.utils.FavoritManager

class DetailKulinerActivity : AppCompatActivity() {

    private var kulinerData: Kuliner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kuliner)

        // Get Kuliner object from intent
        kulinerData = intent.getParcelableExtra("kuliner")
        
        if (kulinerData != null) {
            setupKulinerData(kulinerData!!)
        } else {
            // Fallback to old method if object not found
            val namaKuliner = intent.getStringExtra("nama_kuliner") ?: "Gudeg Yu Djum"
            val rating = intent.getStringExtra("rating") ?: "4.5"
            val lokasi = intent.getStringExtra("lokasi") ?: "Yogyakarta, Jawa Tengah"
            setupKulinerDataLegacy(namaKuliner, rating, lokasi)
        }

        // Setup buttons
        setupBackButton()
        setupMapsButton()
    }
    
    private fun setupKulinerData(kuliner: Kuliner) {
        // Setup views with dynamic data
        findViewById<TextView>(R.id.tv_nama_kuliner).text = kuliner.namaKuliner
        findViewById<TextView>(R.id.tv_rating).text = kuliner.rating
        findViewById<TextView>(R.id.tv_location).text = kuliner.lokasi
        findViewById<TextView>(R.id.tv_description).text = kuliner.description
        
        // Set image
        findViewById<ImageView>(R.id.img_kuliner_main).setImageResource(kuliner.imageResource)
    }
    
    private fun setupKulinerDataLegacy(namaKuliner: String, rating: String, lokasi: String) {
        // Fallback method for old intent extras
        val description = "Deskripsi kuliner akan ditampilkan di sini."
        
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
            kulinerData?.let { kuliner ->
                val uri = "geo:${kuliner.latitude},${kuliner.longitude}?q=${kuliner.latitude},${kuliner.longitude}(${kuliner.namaKuliner})"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    // If Google Maps is not installed, open in browser
                    val browserUri = "https://www.google.com/maps?q=${kuliner.latitude},${kuliner.longitude}"
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(browserUri))
                    startActivity(browserIntent)
                }
            } ?: run {
                android.widget.Toast.makeText(this, "Data lokasi tidak tersedia", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
        }