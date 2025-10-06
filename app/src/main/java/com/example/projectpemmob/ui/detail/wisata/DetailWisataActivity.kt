package com.example.projectpemmob.ui.detail.wisata

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.data.repository.WisataRepository
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.wisata.WisataActivity
import com.example.projectpemmob.utils.FavoritManager

class DetailWisataActivity : AppCompatActivity() {

    private var currentWisata: Wisata? = null

    companion object {
        const val EXTRA_WISATA_ID = "extra_wisata_id"
        const val EXTRA_WISATA = "extra_wisata"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_wisata)

        // Get wisata data from intent with debugging
        currentWisata = when {
            // Prioritas 1: Dari parcel object
            intent.hasExtra(EXTRA_WISATA) -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_WISATA, Wisata::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_WISATA)
                }
            }
            // Prioritas 2: Dari ID wisata
            intent.hasExtra(EXTRA_WISATA_ID) -> {
                val wisataId = intent.getIntExtra(EXTRA_WISATA_ID, -1)
                // Debug log
                android.util.Log.d("DetailWisataActivity", "Received wisataId: $wisataId")
                
                if (wisataId != -1) {
                    val wisata = WisataRepository.getWisataById(wisataId)
                    if (wisata == null) {
                        android.util.Log.e("DetailWisataActivity", "Wisata with ID $wisataId not found in repository")
                    }
                    wisata
                } else {
                    android.util.Log.e("DetailWisataActivity", "Invalid wisataId: $wisataId")
                    null
                }
            }
            // Prioritas 3: Legacy - dari data terpisah (untuk backward compatibility)
            else -> {
                val namaWisata = intent.getStringExtra("nama_wisata")
                namaWisata?.let { WisataRepository.getWisataByName(it) }
            }
        }

        currentWisata?.let { wisata ->
            setupViews(wisata)
            setupBackButton()
            setupFavoritButton()
            setupMapsButton()
            updateFavoritButtonState()
        } ?: run {
            // Jika tidak ada data wisata, tampilkan error dan kembali
            android.util.Log.e("DetailWisataActivity", "currentWisata is null")
            Toast.makeText(this, "Data wisata tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupViews(wisata: Wisata) {
        // Set main image
        findViewById<ImageView>(R.id.img_kuliner_main).setImageResource(wisata.imageResource)
        
        // Set text views
        findViewById<TextView>(R.id.tv_nama_wisata).text = wisata.namaWisata
        findViewById<TextView>(R.id.tv_rating).text = wisata.rating
        findViewById<TextView>(R.id.tv_location).text = wisata.lokasi
        findViewById<TextView>(R.id.tv_description).text = wisata.description
        
        // Set additional info jika ada di layout
        try {
            findViewById<TextView>(R.id.tv_harga)?.text = wisata.harga
            findViewById<TextView>(R.id.tv_jam_buka)?.text = wisata.jamBuka
        } catch (e: Exception) {
            // Layout mungkin belum memiliki field tambahan, tidak masalah
        }
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun setupFavoritButton() {
        findViewById<ImageView>(R.id.btn_favorite).setOnClickListener {
            currentWisata?.let { wisata ->
                if (FavoritManager.isFavorit(this, wisata)) {
                    // Remove from favorites
                    FavoritManager.removeFromFavorit(this, wisata)
                    Toast.makeText(this, "${wisata.namaWisata} dihapus dari favorit", Toast.LENGTH_SHORT).show()
                } else {
                    // Add to favorites
                    FavoritManager.addToFavorit(this, wisata)
                    Toast.makeText(this, "${wisata.namaWisata} ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                }

                updateFavoritButtonState()
            } ?: run {
                Toast.makeText(this, "Data wisata tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMapsButton() {
        findViewById<ImageView>(R.id.btnOpenMaps).setOnClickListener {
            currentWisata?.let { wisata ->
                val uri = "geo:${wisata.latitude},${wisata.longitude}?q=${wisata.latitude},${wisata.longitude}(${wisata.namaWisata})"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    // If Google Maps is not installed, open in browser
                    val browserUri = "https://www.google.com/maps/search/?api=1&query=${wisata.latitude},${wisata.longitude}"
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(browserUri))
                    startActivity(browserIntent)
                }
            }
        }
    }

    private fun updateFavoritButtonState() {
        val btnFavorite = findViewById<ImageView>(R.id.btn_favorite)
        currentWisata?.let { wisata ->
            if (FavoritManager.isFavorit(this, wisata)) {
                btnFavorite.setImageResource(R.drawable.ic_heart_filled)
            } else {
                btnFavorite.setImageResource(R.drawable.ic_heart)
            }
        }
    }
}