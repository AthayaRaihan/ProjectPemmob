package com.example.projectpemmob.ui.kuliner

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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
import com.example.projectpemmob.data.repository.KulinerRepository

class KulinerActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private val allKulinerCards = mutableListOf<CardView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuliner)

        // Initialize search functionality
        initializeSearch()
        
        // Collect all kuliner cards for search
        collectKulinerCards()

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
        try {
            // Setup click listeners untuk 5 kuliner dinamis
            findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok)?.setOnClickListener {
                openDetailKuliner(1) // Mie Ongklok
            }

            findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok_2)?.setOnClickListener {
                openDetailKuliner(2) // Tempe Kemul
            }

            findViewById<androidx.cardview.widget.CardView>(R.id.card_carica)?.setOnClickListener {
                openDetailKuliner(3) // Carica
            }

            findViewById<androidx.cardview.widget.CardView>(R.id.card_sego_megono)?.setOnClickListener {
                openDetailKuliner(4) // Sego Megono
            }

            findViewById<androidx.cardview.widget.CardView>(R.id.card_geblek)?.setOnClickListener {
                openDetailKuliner(5) // Geblek
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openDetailKuliner(kulinerId: Int) {
        val intent = Intent(this, DetailKulinerActivity::class.java)
        intent.putExtra(DetailKulinerActivity.EXTRA_KULINER_ID, kulinerId)
        startActivity(intent)
    }

    // Legacy method untuk backward compatibility
    private fun openDetailKuliner(namaKuliner: String, rating: String, lokasi: String) {
        val kuliner = KulinerRepository.getKulinerByName(namaKuliner)
        kuliner?.let { openDetailKuliner(it.id) }
    }

    private fun initializeSearch() {
        searchEditText = findViewById(R.id.search_kuliner)
        
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                filterKulinerCards(query)
            }
        })
    }

    private fun collectKulinerCards() {
        // Collect all kuliner card views
        allKulinerCards.clear()
        
        findViewById<CardView>(R.id.card_mie_ongklok)?.let { allKulinerCards.add(it) }
        findViewById<CardView>(R.id.card_mie_ongklok_2)?.let { allKulinerCards.add(it) }
        findViewById<CardView>(R.id.card_carica)?.let { allKulinerCards.add(it) }
        findViewById<CardView>(R.id.card_sego_megono)?.let { allKulinerCards.add(it) }
        findViewById<CardView>(R.id.card_geblek)?.let { allKulinerCards.add(it) }
    }

    private fun filterKulinerCards(query: String) {
        if (query.isEmpty()) {
            // Show all cards if search is empty
            allKulinerCards.forEach { it.visibility = View.VISIBLE }
            return
        }

        val searchQuery = query.lowercase()
        
        // Map card IDs to kuliner IDs
        val cardToKulinerMap = mapOf(
            R.id.card_mie_ongklok to 1,      // Mie Ongklok
            R.id.card_mie_ongklok_2 to 2,    // Tempe Kemul
            R.id.card_carica to 3,      // Carica
            R.id.card_sego_megono to 4,      // Sego Megono
            R.id.card_geblek to 5            // Geblek
        )

        allKulinerCards.forEach { card ->
            val kulinerId = cardToKulinerMap[card.id]
            val kuliner = kulinerId?.let { KulinerRepository.getKulinerById(it) }
            
            // Search hanya berdasarkan nama kuliner
            val isMatch = kuliner?.namaKuliner?.lowercase()?.contains(searchQuery) ?: false

            card.visibility = if (isMatch) View.VISIBLE else View.GONE
        }
    }
}