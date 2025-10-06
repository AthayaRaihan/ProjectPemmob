package com.example.projectpemmob.ui.detail.kuliner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Kuliner
import com.example.projectpemmob.data.repository.KulinerRepository

class DetailKulinerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KULINER = "extra_kuliner"
        const val EXTRA_KULINER_ID = "extra_kuliner_id"
    }

    private var currentKuliner: Kuliner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kuliner)

        // Terima data kuliner dengan prioritas
        currentKuliner = when {
            // Prioritas 1: Dari parcel object
            intent.hasExtra(EXTRA_KULINER) -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_KULINER, Kuliner::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_KULINER)
                }
            }

            // Prioritas 2: Dari ID kuliner
            intent.hasExtra(EXTRA_KULINER_ID) -> {
                val kulinerId = intent.getIntExtra(EXTRA_KULINER_ID, -1)
                android.util.Log.d("DetailKulinerActivity", "Received kulinerId: $kulinerId")
                if (kulinerId != -1) {
                    KulinerRepository.getKulinerById(kulinerId)
                } else null
            }

            // Prioritas 3: Legacy compatibility
            else -> {
                val namaKuliner = intent.getStringExtra("nama_kuliner") ?: ""
                
                if (namaKuliner.isNotEmpty()) {
                    KulinerRepository.getKulinerByName(namaKuliner)
                } else null
            }
        }

        currentKuliner?.let { kuliner ->
            setupViews(kuliner)
        } ?: run {
            Toast.makeText(this, "Data kuliner tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupButtons()
    }

    private fun setupViews(kuliner: Kuliner) {
        try {
            // Set basic info
            findViewById<TextView>(R.id.tv_nama_kuliner)?.text = kuliner.namaKuliner
            findViewById<TextView>(R.id.tv_rating)?.text = "Rating: ${kuliner.rating}"
            findViewById<TextView>(R.id.tv_location)?.text = kuliner.lokasi
            findViewById<TextView>(R.id.tv_description)?.text = kuliner.description
            findViewById<TextView>(R.id.tv_harga)?.text = kuliner.harga
            findViewById<TextView>(R.id.tv_jam_buka)?.text = kuliner.jamBuka
            findViewById<TextView>(R.id.tv_kategori)?.text = kuliner.kategori

            // Set image dinamis dari repository
            findViewById<ImageView>(R.id.img_kuliner_main)?.setImageResource(kuliner.imageResource)

            // Setup ingredients


        } catch (e: Exception) {
            android.util.Log.e("DetailKulinerActivity", "Error setting up views", e)
        }
    }



    private fun setupButtons() {
        // Setup back button
        findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        // Setup map button
        findViewById<ImageView>(R.id.btnOpenMaps)?.setOnClickListener {
            currentKuliner?.let { kuliner ->
                openMap(kuliner)
            }
        }
    }

    private fun openMap(kuliner: Kuliner) {
        try {
            val uri = Uri.parse("geo:${kuliner.latitude},${kuliner.longitude}?q=${kuliner.latitude},${kuliner.longitude}(${kuliner.namaKuliner})")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // Fallback ke browser jika Google Maps tidak tersedia
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${kuliner.latitude},${kuliner.longitude}"))
                startActivity(browserIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak dapat membuka peta", Toast.LENGTH_SHORT).show()
        }
    }
}