package com.example.projectpemmob.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.data.model.Kuliner
import com.example.projectpemmob.data.repository.WisataRepository
import com.example.projectpemmob.data.repository.KulinerRepository
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.ui.detail.kuliner.DetailKulinerActivity
import com.example.projectpemmob.ui.navigation.BottomNavigationHandler
import com.example.projectpemmob.utils.FavoritManager
import com.google.firebase.auth.FirebaseAuth

class HomepageActivity : AppCompatActivity() {

    private var topWisataList: List<Wisata> = emptyList()
    private var topKulinerList: List<Kuliner> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Setup bottom navigation (handled externally)
        BottomNavigationHandler(this).setupBottomNavigation()
        // Ensure no transition animation so bottom nav appears static when returning
        overridePendingTransition(0, 0)

        // Load top wisata and kuliner data
        loadTopWisataAndKuliner()

        // Set greeting text
        setupGreetingText()

        // Setup dynamic cards
        setupDynamicCards()

        // Setup favorite button listeners
        setupDynamicFavoriteButtons()
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
    
    private fun loadTopWisataAndKuliner() {
        // Get top 3 wisata sorted by rating (descending)
        topWisataList = WisataRepository.getAllWisata()
            .sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
            .take(3)
            
        // Get top 3 kuliner sorted by rating (descending)
        topKulinerList = KulinerRepository.getAllKuliner()
            .sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
            .take(3)
            
        android.util.Log.d("HomepageActivity", "Loaded ${topWisataList.size} top wisata and ${topKulinerList.size} top kuliner")
    }
    
    private fun setupDynamicCards() {
        // Setup wisata cards
        setupWisataCards()
        
        // Setup kuliner cards
        setupKulinerCards()
    }
    
    private fun setupWisataCards() {
        // Get the wisata cards container
        val wisataContainer = findViewById<LinearLayout>(R.id.wisata_cards_container)
        wisataContainer?.removeAllViews()
        
        // Add dynamic cards for top wisata
        topWisataList.forEach { wisata ->
            val cardView = layoutInflater.inflate(R.layout.item_homepage_wisata_card, null)
            
            // Set card data
            cardView.findViewById<ImageView>(R.id.img_homepage_wisata)?.setImageResource(wisata.imageResource)
            cardView.findViewById<TextView>(R.id.tv_homepage_wisata_nama)?.text = wisata.namaWisata
            cardView.findViewById<TextView>(R.id.tv_homepage_wisata_rating)?.text = wisata.rating
            
            // Set click listener for navigation
            cardView.setOnClickListener {
                openDetailWisata(wisata)
            }
            
            // Setup favorite button
            val heartIcon = cardView.findViewById<ImageView>(R.id.heart_homepage_wisata)
            setupWisataFavoriteButton(wisata, heartIcon)
            
            // Add to container
            wisataContainer?.addView(cardView)
            
            android.util.Log.d("HomepageActivity", "Added wisata card: ${wisata.namaWisata}")
        }
    }
    
    private fun setupWisataFavoriteButton(wisata: Wisata, heartIcon: ImageView) {
        val favoriteIds = getFavoriteIds()
        val isFavorite = favoriteIds.contains(wisata.id)
        
        // Set initial icon
        heartIcon.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_heart
        )
        
        // Set click listener
        heartIcon.setOnClickListener {
            toggleFavoriteWisataIcon(wisata, heartIcon)
        }
    }
    
    private fun setupKulinerCards() {
        // Get the kuliner cards container
        val kulinerContainer = findViewById<LinearLayout>(R.id.kuliner_cards_container)
        kulinerContainer?.removeAllViews()
        
        // Add dynamic cards for top kuliner
        topKulinerList.forEach { kuliner ->
            val cardView = layoutInflater.inflate(R.layout.item_homepage_kuliner_card, null)
            
            // Set card data
            cardView.findViewById<ImageView>(R.id.img_homepage_kuliner)?.setImageResource(kuliner.imageResource)
            cardView.findViewById<TextView>(R.id.tv_homepage_kuliner_nama)?.text = kuliner.namaKuliner
            cardView.findViewById<TextView>(R.id.tv_homepage_kuliner_rating)?.text = kuliner.rating
            
            // Set click listener for navigation
            cardView.setOnClickListener {
                openDetailKuliner(kuliner)
            }
            
            // Setup favorite button
            val heartIcon = cardView.findViewById<ImageView>(R.id.heart_homepage_kuliner)
            setupKulinerFavoriteButton(kuliner, heartIcon)
            
            // Add to container
            kulinerContainer?.addView(cardView)
            
            android.util.Log.d("HomepageActivity", "Added kuliner card: ${kuliner.namaKuliner}")
        }
    }
    
    private fun setupKulinerFavoriteButton(kuliner: Kuliner, heartIcon: ImageView) {
        val favoriteIds = getFavoriteIds()
        val isFavorite = favoriteIds.contains(kuliner.id)
        
        // Set initial icon
        heartIcon.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_heart
        )
        
        // Set click listener
        heartIcon.setOnClickListener {
            toggleFavoriteKulinerIcon(kuliner, heartIcon)
        }
    }

    private fun openDetailWisata(wisata: Wisata) {
        val intent = Intent(this, DetailWisataActivity::class.java)
        intent.putExtra("wisata", wisata)
        startActivity(intent)
    }
    
    private fun openDetailKuliner(kuliner: Kuliner) {
        val intent = Intent(this, DetailKulinerActivity::class.java)
        intent.putExtra("kuliner", kuliner)
        startActivity(intent)
    }

    private fun setupDynamicFavoriteButtons() {
        // Favorite buttons are now setup individually in setupWisataCards and setupKulinerCards
        // This function is called after cards are created to ensure proper setup
        android.util.Log.d("HomepageActivity", "Dynamic favorite buttons setup completed")
    }
    
    private fun toggleFavoriteWisataIcon(wisata: Wisata, icon: ImageView) {
        // Require login before allowing favorite actions
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            AlertDialog.Builder(this)
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
        
        val favoriteIds = getFavoriteIds().toMutableSet()
        
        if (favoriteIds.contains(wisata.id)) {
            favoriteIds.remove(wisata.id)
            icon.setImageResource(R.drawable.ic_heart)
            Toast.makeText(this, "${wisata.namaWisata} dihapus dari favorit", Toast.LENGTH_SHORT).show()
        } else {
            favoriteIds.add(wisata.id)
            icon.setImageResource(R.drawable.ic_favorite_filled)
            Toast.makeText(this, "${wisata.namaWisata} ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
        }
        
        saveFavoriteIds(favoriteIds)
    }
    
    private fun toggleFavoriteKulinerIcon(kuliner: Kuliner, icon: ImageView) {
        // Require login before allowing favorite actions
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            AlertDialog.Builder(this)
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
        
        val favoriteIds = getFavoriteIds().toMutableSet()
        
        if (favoriteIds.contains(kuliner.id)) {
            favoriteIds.remove(kuliner.id)
            icon.setImageResource(R.drawable.ic_heart)
            Toast.makeText(this, "${kuliner.namaKuliner} dihapus dari favorit", Toast.LENGTH_SHORT).show()
        } else {
            favoriteIds.add(kuliner.id)
            icon.setImageResource(R.drawable.ic_favorite_filled)
            Toast.makeText(this, "${kuliner.namaKuliner} ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
        }
        
        saveFavoriteIds(favoriteIds)
    }
    
    private fun getFavoriteIds(): Set<Int> {
        val sharedPref = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        return sharedPref.getStringSet("favorite_ids", emptySet())?.map { it.toInt() }?.toSet() ?: emptySet()
    }
    
    private fun saveFavoriteIds(favoriteIds: Set<Int>) {
        val sharedPref = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putStringSet("favorite_ids", favoriteIds.map { it.toString() }.toSet())
            apply()
        }
    }
}
