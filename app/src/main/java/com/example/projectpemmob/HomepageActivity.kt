package com.example.projectpemmob

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class HomepageActivity : AppCompatActivity() {
    
    private lateinit var contentLayout: LinearLayout
    private lateinit var ivHome: ImageView
    private lateinit var ivLocation: ImageView
    private lateinit var ivRestaurant: ImageView
    private lateinit var ivFavorites: ImageView
    private lateinit var ivProfile: ImageView
    
    private var currentPage = "home"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)
        
        initViews()
        setupBottomNavigation()
        setupCardClickListeners()
        
        // Load home content by default and set icon state
        loadHomeContent()
        updateBottomNavigation("home")
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh current content when returning to this activity
        when (currentPage) {
            "home" -> {
                // Refresh homepage content to update favorite button states
                loadHomeContent()
                updateBottomNavigation("home")
            }
            "favorites" -> loadFavoritesContent()
            "tourism" -> {
                // Refresh tourism content to update favorite button states
                loadTourismContent()
                updateBottomNavigation("tourism")
            }
        }
    }
    
    private fun initViews() {
        contentLayout = findViewById(R.id.content_layout)
        ivHome = findViewById(R.id.iv_home)
        ivLocation = findViewById(R.id.iv_location)
        ivRestaurant = findViewById(R.id.iv_restaurant)
        ivFavorites = findViewById(R.id.iv_favorites)
        ivProfile = findViewById(R.id.iv_profile)
    }
    
    private fun setupBottomNavigation() {
        try {
            // Home Navigation
            findViewById<LinearLayout>(R.id.ll_home)?.setOnClickListener {
                if (currentPage != "home") {
                    loadHomeContent()
                    updateBottomNavigation("home")
                }
            }
            
            // Location Navigation - Load Tourism Content
            findViewById<LinearLayout>(R.id.ll_location)?.setOnClickListener {
                if (currentPage != "tourism") {
                    loadTourismContent()
                    updateBottomNavigation("tourism")
                }
            }
            
            // Restaurant Navigation - Load Culinary Content
            findViewById<LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
                if (currentPage != "culinary") {
                    loadCulinaryContent()
                    updateBottomNavigation("culinary")
                }
            }
            
            // Favorites Navigation - Load Favorites Content
            findViewById<LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
                if (currentPage != "favorites") {
                    loadFavoritesContent()
                    updateBottomNavigation("favorites")
                }
            }
            
            // Profile Navigation - Load Profile Content
            findViewById<LinearLayout>(R.id.ll_profile)?.setOnClickListener {
                if (currentPage != "profile") {
                    loadProfileContent()
                    updateBottomNavigation("profile")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateBottomNavigation(selectedPage: String) {
        currentPage = selectedPage
        
        // Reset all icons to unselected state
        ivHome.setImageResource(R.drawable.ic_home)
        ivLocation.setImageResource(R.drawable.ic_location)
        ivRestaurant.setImageResource(R.drawable.ic_restaurant)
        ivFavorites.setImageResource(R.drawable.ic_favorites)
        ivProfile.setImageResource(R.drawable.ic_profile)
        
        // Set selected icon
        when (selectedPage) {
            "home" -> ivHome.setImageResource(R.drawable.ic_home_selected)
            "tourism" -> ivLocation.setImageResource(R.drawable.ic_location_selected)
            "culinary" -> ivRestaurant.setImageResource(R.drawable.ic_restaurant_selected)
            "favorites" -> ivFavorites.setImageResource(R.drawable.ic_favorites_selected)
            "profile" -> ivProfile.setImageResource(R.drawable.ic_profile_selected)
        }
    }
    
    private fun loadHomeContent() {
        contentLayout.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val homeView = inflater.inflate(R.layout.activity_homepage, contentLayout, false)
        contentLayout.addView(homeView)
        setupCardClickListeners()
    }
    
    private fun loadTourismContent() {
        contentLayout.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val tourismView = inflater.inflate(R.layout.activity_wisata, contentLayout, false)
        contentLayout.addView(tourismView)
        setupTourismClickListeners(tourismView)
    }
    
    private fun loadCulinaryContent() {
        contentLayout.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val culinaryView = inflater.inflate(R.layout.activity_kuliner, contentLayout, false)
        contentLayout.addView(culinaryView)
        setupCulinaryClickListeners(culinaryView)
    }
    
    private fun loadFavoritesContent() {
        contentLayout.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val favoritesView = inflater.inflate(R.layout.activity_favorit, contentLayout, false)
        contentLayout.addView(favoritesView)
        setupFavoritesClickListeners(favoritesView)
        loadFavoritesData(favoritesView)
    }
    
    private fun loadProfileContent() {
        contentLayout.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val profileView = inflater.inflate(R.layout.activity_profile, contentLayout, false)
        contentLayout.addView(profileView)
    }
    
    private fun setupCardClickListeners() {
        try {
            // Card Wisata click listeners di homepage
            contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_homepage_dieng_1)?.setOnClickListener {
                openDetailWisata("Dieng Plateau", "4.8", "Kalimanah, Wonosobo, Jawa Tengah")
            }
            
            contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_homepage_dieng_2)?.setOnClickListener {
                openDetailWisata("Dieng Plateau", "4.8", "Kalimanah, Wonosobo, Jawa Tengah")
            }
            
            // Quick action cards
            contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_dieng_1)?.setOnClickListener {
                loadTourismContent()
                updateBottomNavigation("tourism")
            }
            
            contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok)?.setOnClickListener {
                loadCulinaryContent()
                updateBottomNavigation("culinary")
            }
            
            // Setup favorite buttons untuk wisata di homepage
            setupHomepageFavoriteButtons()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupHomepageFavoriteButtons() {
        try {
            // Setup favorite button untuk Dieng 1 di homepage
            contentLayout.findViewById<ImageView>(R.id.heart_homepage_dieng_1)?.let { favoriteButton ->
                val nama = "Dieng Plateau"
                val rating = "4.8"
                val lokasi = "Wonosobo, Jawa Tengah"
                
                updateFavoriteButtonState(favoriteButton, nama, rating, lokasi)
                favoriteButton.setOnClickListener {
                    toggleFavorite(favoriteButton, nama, rating, lokasi, "wisata")
                }
            }
            
            // Setup favorite button untuk Dieng 2 di homepage
            contentLayout.findViewById<ImageView>(R.id.heart_homepage_dieng_2)?.let { favoriteButton ->
                val nama = "Dieng Plateau"
                val rating = "4.8"
                val lokasi = "Wonosobo, Jawa Tengah"
                
                updateFavoriteButtonState(favoriteButton, nama, rating, lokasi)
                favoriteButton.setOnClickListener {
                    toggleFavorite(favoriteButton, nama, rating, lokasi, "wisata")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun openDetailWisata(namaWisata: String, rating: String, lokasi: String) {
        val intent = Intent(this, DetailWisataActivity::class.java)
        intent.putExtra("nama_wisata", namaWisata)
        intent.putExtra("rating", rating)
        intent.putExtra("lokasi", lokasi)
        startActivity(intent)
    }
    
    private fun setupTourismClickListeners(tourismView: android.view.View) {
        try {
            // Setup click listeners untuk cards di halaman wisata
            tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_dieng_1)?.setOnClickListener {
                openDetailWisata("Dieng Plateau", "4.8", "Wonosobo, Jawa Tengah")
            }
            
            tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_sikidang)?.setOnClickListener {
                openDetailWisata("Kawah Sikidang", "4.7", "Wonosobo, Jawa Tengah")
            }
            
            tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_telaga_warna)?.setOnClickListener {
                openDetailWisata("Telaga Warna", "4.6", "Wonosobo, Jawa Tengah")
            }
            
            // Setup favorite buttons untuk wisata
            setupWisataFavoriteButtons(tourismView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupCulinaryClickListeners(culinaryView: android.view.View) {
        try {
            // Setup click listeners untuk cards di halaman kuliner
            culinaryView.findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok)?.setOnClickListener {
                openDetailKuliner("Mie Ongklok Abang Adek", "4.5", "Wonosobo, Jawa Tengah")
            }
            
            culinaryView.findViewById<androidx.cardview.widget.CardView>(R.id.card_sate_buntel)?.setOnClickListener {
                openDetailKuliner("Sate Buntel Khas Wonosobo", "4.6", "Wonosobo, Jawa Tengah")
            }
            
            // Kuliner tidak memiliki fungsi favorit
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupFavoritesClickListeners(favoritesView: android.view.View) {
        try {
            // Setup click listeners akan ditambahkan secara dinamis
            // saat cards favorit dibuat
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadFavoritesData(favoritesView: android.view.View) {
        val emptyStateLayout = favoritesView.findViewById<LinearLayout>(R.id.empty_state_layout)
        val favoritListLayout = favoritesView.findViewById<LinearLayout>(R.id.favorit_list_layout)
        
        val favoritSet = FavoritManager.getFavoritList(this)
        
        if (favoritSet.isEmpty()) {
            // Show empty state
            emptyStateLayout?.visibility = View.VISIBLE
            favoritListLayout?.visibility = View.GONE
        } else {
            // Show favorit list
            emptyStateLayout?.visibility = View.GONE
            favoritListLayout?.visibility = View.VISIBLE
            populateFavoritList(favoritSet, favoritListLayout)
        }
    }
    
    private fun populateFavoritList(favoritSet: Set<String>, favoritListLayout: LinearLayout?) {
        favoritListLayout?.removeAllViews()
        
        for (favoritItem in favoritSet) {
            val parts = favoritItem.split("|")
            if (parts.size >= 3) {
                val namaWisata = parts[0]
                val rating = parts[1]
                val lokasi = parts[2]
                addFavoritCard(namaWisata, rating, lokasi, favoritListLayout)
            }
        }
    }
    
    private fun addFavoritCard(namaWisata: String, rating: String, lokasi: String, favoritListLayout: LinearLayout?) {
        try {
            val cardView = layoutInflater.inflate(R.layout.item_favorit_card, favoritListLayout, false) as androidx.cardview.widget.CardView
            
            // Set data to card
            cardView.findViewById<android.widget.TextView>(R.id.tv_nama_wisata_favorit)?.text = namaWisata
            cardView.findViewById<android.widget.TextView>(R.id.tv_rating_favorit)?.text = rating
            
            // Add click listener to open detail
            cardView.setOnClickListener {
                openDetailWisata(namaWisata, rating, lokasi)
            }
            
            // Add remove from favorit functionality
            cardView.findViewById<View>(R.id.btn_remove_favorit)?.setOnClickListener {
                removeFromFavoritAndRefresh(namaWisata, rating, lokasi)
            }
            
            favoritListLayout?.addView(cardView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun removeFromFavoritAndRefresh(namaWisata: String, rating: String, lokasi: String) {
        FavoritManager.removeFromFavorit(this, namaWisata, rating, lokasi)
        
        // Refresh favorit content if currently showing favorites
        if (currentPage == "favorites") {
            loadFavoritesContent()
        }
        
        Toast.makeText(this, "$namaWisata dihapus dari favorit", Toast.LENGTH_SHORT).show()
    }
    
    private fun openDetailKuliner(namaKuliner: String, rating: String, lokasi: String) {
        val intent = Intent(this, DetailKulinerActivity::class.java)
        intent.putExtra("nama_kuliner", namaKuliner)
        intent.putExtra("rating", rating)
        intent.putExtra("lokasi", lokasi)
        startActivity(intent)
    }
    
    private fun setupWisataFavoriteButtons(tourismView: android.view.View) {
        try {
            // Setup favorite button untuk Dieng
            tourismView.findViewById<ImageView>(R.id.heart_dieng_1)?.let { favoriteButton ->
                val nama = "Dieng Plateau"
                val rating = "4.8"
                val lokasi = "Wonosobo, Jawa Tengah"
                
                updateFavoriteButtonState(favoriteButton, nama, rating, lokasi)
                favoriteButton.setOnClickListener {
                    toggleFavorite(favoriteButton, nama, rating, lokasi, "wisata")
                }
            }
            
            // Setup favorite button untuk Sikunir
            tourismView.findViewById<ImageView>(R.id.heart_sikidang)?.let { favoriteButton ->
                val nama = "Kawah Sikidang"
                val rating = "4.7"
                val lokasi = "Wonosobo, Jawa Tengah"
                
                updateFavoriteButtonState(favoriteButton, nama, rating, lokasi)
                favoriteButton.setOnClickListener {
                    toggleFavorite(favoriteButton, nama, rating, lokasi, "wisata")
                }
            }
            
            // Setup favorite button untuk Telaga Warna
            tourismView.findViewById<ImageView>(R.id.heart_telaga_warna)?.let { favoriteButton ->
                val nama = "Telaga Warna"
                val rating = "4.6"
                val lokasi = "Wonosobo, Jawa Tengah"
                
                updateFavoriteButtonState(favoriteButton, nama, rating, lokasi)
                favoriteButton.setOnClickListener {
                    toggleFavorite(favoriteButton, nama, rating, lokasi, "wisata")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateFavoriteButtonState(favoriteButton: ImageView, nama: String, rating: String, lokasi: String) {
        val isFavorite = FavoritManager.isFavorit(this, nama, rating, lokasi)
        favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorites
        )
    }
    
    private fun toggleFavorite(favoriteButton: ImageView, nama: String, rating: String, lokasi: String, tipe: String) {
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
}