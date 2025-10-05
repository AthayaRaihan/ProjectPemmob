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

        // Setup bottom navigation
        setupBottomNavigation()

        // Setup card click listeners
        setupCardClickListeners()
    }

    private fun setupBottomNavigation() {
        try {
            // Icon Home untuk kembali ke homepage
            findViewById<android.widget.LinearLayout>(R.id.ll_home)?.setOnClickListener {
                val intent = Intent(this, HomepageActivity::class.java)
                startActivity(intent)
                finish() // Tutup activity kuliner
            }

            // Icon Location untuk ke halaman wisata
            findViewById<android.widget.LinearLayout>(R.id.ll_location)?.setOnClickListener {
                val intent = Intent(this, WisataActivity::class.java)
                startActivity(intent)
            }

            // Icon Favorites untuk ke halaman favorit
            findViewById<android.widget.LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                val intent = Intent(this, FavoritActivity::class.java)
                startActivity(intent)
            }

            // Icon Restaurant - sudah di halaman kuliner, tidak perlu action
            findViewById<android.widget.LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                // Sudah di halaman kuliner, tidak perlu navigasi
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