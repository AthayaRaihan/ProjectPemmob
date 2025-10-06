package com.example.projectpemmob.ui.wisata

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
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.data.repository.WisataRepository
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.profil.ProfileActivity
import com.example.projectpemmob.utils.FavoritManager

class WisataActivity : AppCompatActivity() {
    
    private lateinit var wisataList: List<Wisata>
    private lateinit var searchEditText: EditText
    private val allWisataCards = mutableListOf<CardView>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wisata)

        // Load data wisata dari repository
        wisataList = WisataRepository.getAllWisata()
        android.util.Log.d("WisataActivity", "Loaded ${wisataList.size} wisata items")
        
        // Test repository
        for (i in 1..8) {
            val wisata = WisataRepository.getWisataById(i)
            android.util.Log.d("WisataActivity", "Wisata ID $i: ${wisata?.namaWisata ?: "NOT FOUND"}")
        }

        // Initialize search functionality
        initializeSearch()
        
        // Collect all wisata cards for search
        collectWisataCards()

        // Setup bottom navigation
        setupBottomNavigation()

        // Setup card click listeners
        setupCardClickListeners()

        // Setup heart icon listeners
        setupHeartIconListeners()
    }

    override fun onResume() {
        super.onResume()
        // Update heart icon states when returning to this activity
        updateAllHeartIcons()
    }

    private fun setupBottomNavigation() {
        try {
            // Icon Home untuk kembali ke homepage
            findViewById<android.widget.LinearLayout>(R.id.ll_home)?.setOnClickListener {
                val intent = Intent(this, HomepageActivity::class.java)
                startActivity(intent)
                finish() // Tutup activity wisata
            }

            // Icon Location - sudah di halaman wisata, tidak perlu action
            findViewById<android.widget.LinearLayout>(R.id.ll_location)?.setOnClickListener {
                // Sudah di halaman wisata, tidak perlu navigasi
            }

            // Icon Restaurant untuk ke halaman kuliner
            findViewById<android.widget.LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                val intent = Intent(this, KulinerActivity::class.java)
                startActivity(intent)
            }

            // Icon Favorites untuk ke halaman favorit
            findViewById<android.widget.LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                val intent = Intent(this, FavoritActivity::class.java)
                startActivity(intent)
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
        
        // Card Dieng Plateau (ID: 7)
        findViewById<androidx.cardview.widget.CardView>(R.id.card_dieng_1)?.setOnClickListener {
            openDetailWisata(7)
        }

        // Card Kawah Sikidang (ID: 6)
        findViewById<androidx.cardview.widget.CardView>(R.id.card_sikidang)?.setOnClickListener {
            openDetailWisata(6)
        }

        // Card Telaga Warna - sekarang untuk Candi Arjuna (ID: 5)
        findViewById<androidx.cardview.widget.CardView>(R.id.card_telaga_warna)?.setOnClickListener {
            openDetailWisata(5) // Candi Arjuna
        }

        // Card Telaga Menjer (ID: 1)
        findViewById<androidx.cardview.widget.CardView>(R.id.card_telaga_menjer)?.setOnClickListener {
            openDetailWisata(1)
        }

        // Card Bukit Sikunir (ID: 2)
        findViewById<androidx.cardview.widget.CardView>(R.id.card_bukit_sikunir)?.setOnClickListener {
            openDetailWisata(2)
        }

        // Card Gunung Prau (ID: 3)
        findViewById<androidx.cardview.widget.CardView>(R.id.card_gunung_prau)?.setOnClickListener {
            openDetailWisata(3)
        }

        // Card Bukit Scooter (ID: 4)
        findViewById<androidx.cardview.widget.CardView>(R.id.card_bukit_scooter)?.setOnClickListener {
            openDetailWisata(4)
        }

        // Card Candi Arjuna (ID: 8)
        findViewById<androidx.cardview.widget.CardView>(R.id.card_arjuna)?.setOnClickListener {
            openDetailWisata(8)
        }

        // Setup favorite buttons
        setupFavoriteButtons()
    }

    private fun openDetailWisata(wisataId: Int) {
        android.util.Log.d("WisataActivity", "Opening detail for wisataId: $wisataId")
        
        val intent = Intent(this, DetailWisataActivity::class.java)
        intent.putExtra(DetailWisataActivity.EXTRA_WISATA_ID, wisataId)
        startActivity(intent)
    }

    // Legacy method untuk backward compatibility
    private fun openDetailWisata(namaWisata: String, rating: String, lokasi: String) {
        val wisata = WisataRepository.getWisataByName(namaWisata)
        wisata?.let { openDetailWisata(it.id) }
    }

    private fun setupFavoriteButtons() {
        // Setup semua heart icon listeners untuk card wisata
        setupHeartIcon(R.id.heart_dieng_1, 7)           // Dieng Plateau
        setupHeartIcon(R.id.heart_sikidang, 6)          // Kawah Sikidang
        setupHeartIcon(R.id.heart_telaga_warna, 5)      // Telaga Warna
        setupHeartIcon(R.id.heart_telaga_menjer, 1)     // Telaga Menjer
        setupHeartIcon(R.id.heart_bukit_sikunir, 2)     // Bukit Sikunir
        setupHeartIcon(R.id.heart_gunung_prau, 3)       // Gunung Prau
        setupHeartIcon(R.id.heart_bukit_scooter, 4)     // Bukit Scooter
        setupHeartIcon(R.id.heart_arjuna, 8)            // Candi Arjuna
    }

    private fun setupHeartIconListeners() {
        // Legacy method - gunakan setupFavoriteButtons() sebagai gantinya
        setupFavoriteButtons()
        // Bukit Scooter
    }

    private fun setupHeartIcon(heartIconId: Int, wisataId: Int) {
        val heartIcon = findViewById<ImageView>(heartIconId) ?: return
        val wisata = WisataRepository.getWisataById(wisataId) ?: return

        // Set initial state
        updateHeartIcon(heartIcon, wisata)

        // Set click listener
        heartIcon.setOnClickListener {
            toggleFavorit(heartIcon, wisata)
        }
    }

    private fun updateHeartIcon(heartIcon: ImageView, wisata: Wisata) {
        if (FavoritManager.isFavorit(this, wisata)) {
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        } else {
            heartIcon.setImageResource(R.drawable.ic_heart)
        }
    }

    private fun toggleFavorit(heartIcon: ImageView, wisata: Wisata) {
        if (FavoritManager.isFavorit(this, wisata)) {
            // Remove from favorit
            FavoritManager.removeFromFavorit(this, wisata)
            heartIcon.setImageResource(R.drawable.ic_heart)
        } else {
            // Add to favorit
            FavoritManager.addToFavorit(this, wisata)
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        }
    }

    private fun updateAllHeartIcons() {
        // Update semua heart icon berdasarkan mapping ID yang benar
        updateHeartIconById(R.id.heart_dieng_1, 7)         // Dieng Plateau
        updateHeartIconById(R.id.heart_sikidang, 6)        // Kawah Sikidang
        updateHeartIconById(R.id.heart_telaga_warna, 5)    // Candi Arjuna
        updateHeartIconById(R.id.heart_telaga_menjer, 1)   // Telaga Menjer
        updateHeartIconById(R.id.heart_bukit_sikunir, 2)   // Bukit Sikunir
        updateHeartIconById(R.id.heart_gunung_prau, 3)     // Gunung Prau
        updateHeartIconById(R.id.heart_bukit_scooter, 4)
        updateHeartIconById(R.id.heart_arjuna, 8)
    }
    
    private fun updateHeartIconById(heartIconId: Int, wisataId: Int) {
        val heartIcon = findViewById<ImageView>(heartIconId) ?: return
        val wisata = WisataRepository.getWisataById(wisataId) ?: return
        updateHeartIcon(heartIcon, wisata)
    }

    private fun initializeSearch() {
        searchEditText = findViewById(R.id.search_wisata)
        
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                filterWisataCards(query)
            }
        })
    }

    private fun collectWisataCards() {
        // Collect all wisata card views
        allWisataCards.clear()
        
        findViewById<CardView>(R.id.card_dieng_1)?.let { allWisataCards.add(it) }
        findViewById<CardView>(R.id.card_sikidang)?.let { allWisataCards.add(it) }
        findViewById<CardView>(R.id.card_telaga_warna)?.let { allWisataCards.add(it) }
        findViewById<CardView>(R.id.card_telaga_menjer)?.let { allWisataCards.add(it) }
        findViewById<CardView>(R.id.card_bukit_sikunir)?.let { allWisataCards.add(it) }
        findViewById<CardView>(R.id.card_gunung_prau)?.let { allWisataCards.add(it) }
        findViewById<CardView>(R.id.card_bukit_scooter)?.let { allWisataCards.add(it) }
        findViewById<CardView>(R.id.card_arjuna)?.let { allWisataCards.add(it) }
    }

    private fun filterWisataCards(query: String) {
        if (query.isEmpty()) {
            // Show all cards if search is empty
            allWisataCards.forEach { it.visibility = View.VISIBLE }
            return
        }

        val searchQuery = query.lowercase()
        
        // Map card IDs to wisata IDs
        val cardToWisataMap = mapOf(
            R.id.card_dieng_1 to 7,      // Dieng Plateau
            R.id.card_sikidang to 6,      // Kawah Sikidang  
            R.id.card_telaga_warna to 5,  // Telaga Warna
            R.id.card_telaga_menjer to 1, // Telaga Menjer
            R.id.card_bukit_sikunir to 2, // Bukit Sikunir
            R.id.card_gunung_prau to 3,   // Gunung Prau
            R.id.card_bukit_scooter to 4, // Bukit Scooter
            R.id.card_arjuna to 8         // Candi Arjuna
        )

        allWisataCards.forEach { card ->
            val wisataId = cardToWisataMap[card.id]
            val wisata = wisataId?.let { WisataRepository.getWisataById(it) }
            
            // Search hanya berdasarkan nama wisata
            val isMatch = wisata?.namaWisata?.lowercase()?.contains(searchQuery) ?: false

            card.visibility = if (isMatch) View.VISIBLE else View.GONE
        }
    }
}

