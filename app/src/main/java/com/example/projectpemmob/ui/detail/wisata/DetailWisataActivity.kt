package com.example.projectpemmob.ui.detail.wisata

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.wisata.WisataActivity
import com.example.projectpemmob.utils.FavoritManager
import com.google.firebase.auth.FirebaseAuth
import com.example.projectpemmob.ui.auth.LoginActivity

class DetailWisataActivity : AppCompatActivity() {

    private var wisataData: Wisata? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_wisata)

        // Get Wisata object from intent - check both possible keys
        wisataData = intent.getParcelableExtra("wisata") ?: intent.getParcelableExtra("wisata_data")
        
        if (wisataData != null) {
            setupWisataData(wisataData!!)
        } else {
            // Fallback to old method if object not found
            val namaWisata = intent.getStringExtra("nama_wisata") ?: "Dieng Plateau"
            val rating = intent.getStringExtra("rating") ?: "4.8"
            val lokasi = intent.getStringExtra("lokasi") ?: "Kalimanah, Wonosobo, Jawa Tengah"
            setupWisataDataLegacy(namaWisata, rating, lokasi)
        }
        
        // Setup buttons
        setupBackButton()
        setupFavoritButton()
        setupMapsButton()

        // Load per-user favorites first then update button state
        com.example.projectpemmob.utils.FavoritManager.loadForCurrentUser(this) {
            updateFavoritButtonState()
        }
    }
    
    private fun setupWisataData(wisata: Wisata) {
        // Setup views with dynamic data
        findViewById<TextView>(R.id.tv_nama_wisata).text = wisata.namaWisata
        findViewById<TextView>(R.id.tv_rating).text = wisata.rating
        findViewById<TextView>(R.id.tv_location).text = wisata.lokasi
        findViewById<TextView>(R.id.tv_description).text = wisata.description
        
        // Set image
        findViewById<ImageView>(R.id.img_kuliner_main).setImageResource(wisata.imageResource)
    }
    
    private fun setupWisataDataLegacy(namaWisata: String, rating: String, lokasi: String) {
        // Fallback method for old intent extras
        val description = "Deskripsi wisata akan ditampilkan di sini."
        
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
            // If user not logged in, prompt to login before allowing favorit actions
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Perlu Login")
                    .setMessage("Anda harus login jika ingin menambahkan favorit.")
                    .setPositiveButton("OK") { _, _ ->
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton("Batal", null)
                    .show()
                return@setOnClickListener
            }

            wisataData?.let { wisata ->
                if (FavoritManager.isFavorit(this, wisata.namaWisata, wisata.rating, wisata.lokasi)) {
                    // Remove from favorites
                    FavoritManager.removeFromFavorit(this, wisata.namaWisata, wisata.rating, wisata.lokasi)
                    Toast.makeText(this, "${wisata.namaWisata} dihapus dari favorit", Toast.LENGTH_SHORT).show()
                } else {
                    // Add to favorites
                    FavoritManager.addToFavorit(this, wisata.namaWisata, wisata.rating, wisata.lokasi)
                    Toast.makeText(this, "${wisata.namaWisata} ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                }

                updateFavoritButtonState()
            }
        }
    }

    private fun setupMapsButton() {
        findViewById<ImageView>(R.id.btnOpenMaps).setOnClickListener {
            wisataData?.let { wisata ->
                val uri = "geo:${wisata.latitude},${wisata.longitude}?q=${wisata.latitude},${wisata.longitude}(${wisata.namaWisata})"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    // If Google Maps is not installed, open in browser
                    val browserUri = "https://www.google.com/maps?q=${wisata.latitude},${wisata.longitude}"
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(browserUri))
                    startActivity(browserIntent)
                }
            } ?: run {
                Toast.makeText(this, "Data lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoritButtonState() {
        val btnFavorite = findViewById<ImageView>(R.id.btn_favorite)
        wisataData?.let { wisata ->
            if (FavoritManager.isFavorit(this, wisata.namaWisata, wisata.rating, wisata.lokasi)) {
                btnFavorite.setImageResource(R.drawable.ic_heart_filled)
            } else {
                btnFavorite.setImageResource(R.drawable.ic_heart)
            }
        }
    }
}