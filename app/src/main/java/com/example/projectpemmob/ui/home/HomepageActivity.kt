package com.example.projectpemmob.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.projectpemmob.R
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.ui.navigation.BottomNavigationHandler
import com.example.projectpemmob.utils.FavoritManager
import com.google.firebase.auth.FirebaseAuth

class HomepageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

    // Setup bottom navigation (handled externally)
    BottomNavigationHandler(this).setupBottomNavigation()
    // Ensure no transition animation so bottom nav appears static when returning
    overridePendingTransition(0, 0)

        // Set greeting text
        setupGreetingText()

        // Setup card listeners
        setupCardClickListeners()

        // Setup favorite button listeners, but ensure per-user favorites are loaded first
        FavoritManager.loadForCurrentUser(this) {
            setupHomepageFavoriteButtons()
        }
    }

    private fun setupGreetingText() {
        val tvGreetingName = findViewById<TextView>(R.id.tvGreetingName)
        val user = FirebaseAuth.getInstance().currentUser

        val displayName = when {
            user?.displayName != null && user.displayName!!.isNotEmpty() -> user.displayName
            user?.email != null -> user.email?.substringBefore("@")
            else -> "User"
        }

        tvGreetingName.text = "Hai $displayName,"
    }

    private fun setupCardClickListeners() {
        // Card Wisata
        findViewById<CardView>(R.id.card_homepage_dieng_1)?.setOnClickListener {
            openDetailWisata("Dieng Plateau", "4.8", "Kalimanah, Wonosobo, Jawa Tengah")
        }

        findViewById<CardView>(R.id.card_homepage_dieng_1)?.setOnClickListener {
            openDetailWisata("Dieng Plateau", "4.8", "Kalimanah, Wonosobo, Jawa Tengah")
        }
    }

    private fun setupHomepageFavoriteButtons() {
        // Favorit button untuk Dieng 1
        findViewById<ImageView>(R.id.heart_homepage_dieng_1)?.let { favoriteButton ->
            val nama = "Dieng Plateau"
            val rating = "4.8"
            val lokasi = "Wonosobo, Jawa Tengah"

            updateFavoriteButtonState(favoriteButton, nama, rating, lokasi)
            favoriteButton.setOnClickListener {
                toggleFavorite(favoriteButton, nama, rating, lokasi)
            }
        }

        // Favorit button untuk Dieng 2
        findViewById<ImageView>(R.id.heart_homepage_dieng_1)?.let { favoriteButton ->
            val nama = "Dieng Plateau"
            val rating = "4.8"
            val lokasi = "Wonosobo, Jawa Tengah"

            updateFavoriteButtonState(favoriteButton, nama, rating, lokasi)
            favoriteButton.setOnClickListener {
                toggleFavorite(favoriteButton, nama, rating, lokasi)
            }
        }
    }

    private fun updateFavoriteButtonState(
        favoriteButton: ImageView,
        nama: String,
        rating: String,
        lokasi: String
    ) {
        val isFavorite = FavoritManager.isFavorit(this, nama, rating, lokasi)
        favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorites
        )
    }

    private fun toggleFavorite(
        favoriteButton: ImageView,
        nama: String,
        rating: String,
        lokasi: String
    ) {
        // Require login before allowing favorit actions
        val currentUser = FirebaseAuth.getInstance().currentUser
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

        val isFavorite = FavoritManager.isFavorit(this, nama, rating, lokasi)

        if (isFavorite) {
            FavoritManager.removeFromFavorit(this, nama, rating, lokasi)
            favoriteButton.setImageResource(R.drawable.ic_favorites)
            Toast.makeText(this, "$nama dihapus dari favorit", Toast.LENGTH_SHORT).show()
        } else {
            FavoritManager.addToFavorit(this, nama, rating, lokasi)
            favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
            Toast.makeText(this, "$nama ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDetailWisata(namaWisata: String, rating: String, lokasi: String) {
        val intent = Intent(this, DetailWisataActivity::class.java)
        intent.putExtra("nama_wisata", namaWisata)
        intent.putExtra("rating", rating)
        intent.putExtra("lokasi", lokasi)
        startActivity(intent)
    }
}
