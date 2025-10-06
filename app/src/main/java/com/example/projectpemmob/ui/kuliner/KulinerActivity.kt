package com.example.projectpemmob.ui.kuliner

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.projectpemmob.R
import com.example.projectpemmob.ui.detail.kuliner.DetailKulinerActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.profil.ProfileActivity
import com.example.projectpemmob.ui.wisata.WisataActivity
import com.example.projectpemmob.utils.FavoritManager

class KulinerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuliner)

    // Setup bottom navigation (shared handler)
    com.example.projectpemmob.ui.navigation.BottomNavigationHandler(this).setupBottomNavigation()
    // Ensure no transition animation so bottom nav appears static when returning
    overridePendingTransition(0, 0)

        // Setup card click listeners
        setupCardClickListeners()
    }

    // Navigation handled by BottomNavigationHandler

    private fun setupCardClickListeners() {
        // Card Kuliner click listeners
        findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok)?.setOnClickListener {
            openDetailKuliner("Mie Ongklok Abang Adek", "4.8", "Wonosobo, Jawa Tengah")
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok_2)?.setOnClickListener {
            openDetailKuliner("Mie Ongklok Ibu Bapak", "4.7", "Wonosobo, Jawa Tengah")
        }


        findViewById<androidx.cardview.widget.CardView>(R.id.card_sate_buntel)?.setOnClickListener {
            openDetailKuliner("Sate Buntel Khas Wonosobo", "4.6", "Wonosobo, Jawa Tengah")
        }
    }

    private fun openDetailKuliner(namaKuliner: String, rating: String, lokasi: String) {
        val intent = Intent(this, DetailKulinerActivity::class.java)
        intent.putExtra("nama_kuliner", namaKuliner)
        intent.putExtra("rating", rating)
        intent.putExtra("lokasi", lokasi)
        startActivity(intent)
    }
}