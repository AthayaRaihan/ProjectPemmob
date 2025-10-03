package com.example.projectpemmob

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class FavoritActivity : AppCompatActivity() {
    
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var favoritListLayout: LinearLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorit_main)
        
        // Initialize views
        emptyStateLayout = findViewById(R.id.empty_state_layout)
        favoritListLayout = findViewById(R.id.favorit_list_layout)
        
        // Setup bottom navigation
        setupBottomNavigation()
        
        // Load favorit data
        loadFavoritData()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh favorit list when returning to this activity
        loadFavoritData()
    }
    
    private fun loadFavoritData() {
        val sharedPreferences = getSharedPreferences("favorit_wisata", MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet("favorit_list", setOf()) ?: setOf()
        
        if (favoritSet.isEmpty()) {
            // Show empty state
            emptyStateLayout.visibility = View.VISIBLE
            favoritListLayout.visibility = View.GONE
        } else {
            // Show favorit list
            emptyStateLayout.visibility = View.GONE
            favoritListLayout.visibility = View.VISIBLE
            populateFavoritList(favoritSet)
        }
    }
    
    private fun populateFavoritList(favoritSet: Set<String>) {
        favoritListLayout.removeAllViews()
        
        for (favoritItem in favoritSet) {
            val parts = favoritItem.split("|")
            if (parts.size >= 3) {
                val namaWisata = parts[0]
                val rating = parts[1]
                val lokasi = parts[2]
                addFavoritCard(namaWisata, rating, lokasi)
            }
        }
    }
    
    private fun addFavoritCard(namaWisata: String, rating: String, lokasi: String) {
        val cardView = layoutInflater.inflate(R.layout.item_favorit_card, favoritListLayout, false) as CardView
        
        // Set data to card
        cardView.findViewById<TextView>(R.id.tv_nama_wisata_favorit).text = namaWisata
        cardView.findViewById<TextView>(R.id.tv_rating_favorit).text = rating
        
        // Add click listener to open detail
        cardView.setOnClickListener {
            openDetailWisata(namaWisata, rating, lokasi)
        }
        
        // Add remove from favorit functionality
        cardView.findViewById<View>(R.id.btn_remove_favorit).setOnClickListener {
            removeFromFavorit(namaWisata, rating, lokasi)
        }
        
        favoritListLayout.addView(cardView)
    }
    
    private fun removeFromFavorit(namaWisata: String, rating: String, lokasi: String) {
        val sharedPreferences = getSharedPreferences("favorit_wisata", MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet("favorit_list", setOf())?.toMutableSet() ?: mutableSetOf()
        
        val itemToRemove = "$namaWisata|$rating|$lokasi"
        favoritSet.remove(itemToRemove)
        
        sharedPreferences.edit()
            .putStringSet("favorit_list", favoritSet)
            .apply()
        
        // Refresh the list
        loadFavoritData()
    }
    
    private fun openDetailWisata(namaWisata: String, rating: String, lokasi: String) {
        val intent = Intent(this, DetailWisataActivity::class.java)
        intent.putExtra("nama_wisata", namaWisata)
        intent.putExtra("rating", rating)
        intent.putExtra("lokasi", lokasi)
        startActivity(intent)
    }
    
    private fun setupBottomNavigation() {
        try {
            // Icon Home untuk kembali ke homepage
            findViewById<LinearLayout>(R.id.ll_home)?.setOnClickListener {
                val intent = Intent(this, HomepageActivity::class.java)
                startActivity(intent)
                finish()
            }
            
            // Icon Location untuk ke halaman wisata
            findViewById<LinearLayout>(R.id.ll_location)?.setOnClickListener {
                val intent = Intent(this, WisataActivity::class.java)
                startActivity(intent)
                finish()
            }
            
            // Icon Restaurant untuk ke halaman kuliner
            findViewById<LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                val intent = Intent(this, KulinerActivity::class.java)
                startActivity(intent)
                finish()
            }
            
            // Icon Favorites - sudah di halaman favorit, tidak perlu action
            findViewById<LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                // Sudah di halaman favorit, tidak perlu navigasi
            }
            
            // Icon Profile - placeholder untuk fitur masa depan
            findViewById<LinearLayout>(R.id.ll_profile)?.setOnClickListener {
                // Placeholder untuk halaman profile (belum dibuat)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}