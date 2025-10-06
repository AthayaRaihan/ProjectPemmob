package com.example.projectpemmob.ui.wisata

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.projectpemmob.R
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.profil.ProfileActivity
import com.example.projectpemmob.utils.FavoritManager

class WisataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wisata)

    // Setup bottom navigation (shared handler)
    com.example.projectpemmob.ui.navigation.BottomNavigationHandler(this).setupBottomNavigation()
    // Ensure no transition animation so bottom nav appears static when returning
    overridePendingTransition(0, 0)

        // Setup card click listeners
        setupCardClickListeners()

        // Setup heart icon listeners after loading per-user favorites
        FavoritManager.loadForCurrentUser(this) {
            setupHeartIconListeners()
        }
    }

    override fun onResume() {
        super.onResume()
        // Update heart icon states when returning to this activity
        updateAllHeartIcons()
    }

    // Navigation handled by BottomNavigationHandler

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
        // Require login before allowing favorit changes
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Perlu Login")
                .setMessage("Anda harus login jika ingin menambahkan favorit.")
                .setPositiveButton("OK") { _, _ ->
                    val intent = Intent(this, com.example.projectpemmob.ui.auth.LoginActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Batal", null)
                .show()
            return
        }

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