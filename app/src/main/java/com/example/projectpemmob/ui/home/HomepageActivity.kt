package com.example.projectpemmob.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.data.model.Kuliner
import com.example.projectpemmob.data.model.SearchResult
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
    private var allWisataList: List<Wisata> = emptyList()
    private var allKulinerList: List<Kuliner> = emptyList()
    
    // Search components
    private lateinit var etSearch: EditText
    private lateinit var searchDropdownContainer: CardView
    private lateinit var rvSearchDropdown: RecyclerView
    private lateinit var searchAdapter: SearchDropdownAdapter

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
        
        // Setup search functionality
        setupSearchFunctionality()

        // Setup dynamic cards
        setupDynamicCards()

        // Setup favorite button listeners
        setupDynamicFavoriteButtons()
        
        // Setup back pressed callback
        setupBackPressedCallback()
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
        // Get all wisata and kuliner data for search
        allWisataList = WisataRepository.getAllWisata()
        allKulinerList = KulinerRepository.getAllKuliner()
        
        // Get top 3 wisata sorted by rating (descending)
        topWisataList = allWisataList
            .sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
            .take(3)
            
        // Get top 3 kuliner sorted by rating (descending)
        topKulinerList = allKulinerList
            .sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
            .take(3)
            
        android.util.Log.d("HomepageActivity", "Loaded ${topWisataList.size} top wisata and ${topKulinerList.size} top kuliner")
    }
    
    private fun setupSearchFunctionality() {
        // Initialize search components
        etSearch = findViewById(R.id.et_search)
        searchDropdownContainer = findViewById(R.id.search_dropdown_container)
        rvSearchDropdown = findViewById(R.id.rv_search_dropdown)
        
        // Setup RecyclerView
        searchAdapter = SearchDropdownAdapter { searchResult ->
            handleSearchItemClick(searchResult)
        }
        rvSearchDropdown.layoutManager = LinearLayoutManager(this)
        rvSearchDropdown.adapter = searchAdapter
        
        // Setup search text watcher
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    hideSearchDropdown()
                } else if (query.length >= 1) { // Start searching from 1 character
                    performSearch(query)
                }
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // Hide dropdown when losing focus
        etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideSearchDropdown()
            }
        }
        
        // Position dropdown correctly when shown
        etSearch.viewTreeObserver.addOnGlobalLayoutListener {
            positionDropdown()
        }
    }
    
    private fun positionDropdown() {
        val searchContainer = findViewById<LinearLayout>(R.id.search_container)
        if (searchContainer != null && searchDropdownContainer != null) {
            val location = IntArray(2)
            searchContainer.getLocationOnScreen(location)
            
            // Calculate position relative to search bar with much larger gap
            val searchBarBottom = location[1] + searchContainer.height
            val statusBarHeight = getStatusBarHeight()
            
            val params = searchDropdownContainer.layoutParams as RelativeLayout.LayoutParams
            params.topMargin = searchBarBottom - statusBarHeight + 40 // 40dp gap below search bar
            searchDropdownContainer.layoutParams = params
        }
    }
    
    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
    
    private fun performSearch(query: String) {
        val searchResults = mutableListOf<Pair<SearchResult, Int>>() // Pair of SearchResult and score
        val queryLower = query.lowercase().trim()
        
        android.util.Log.d("SearchDebug", "Performing search for: '$queryLower'")
        
        // Search in wisata with strict filtering
        allWisataList.forEach { wisata ->
            val matchScore = calculateMatchScore(queryLower, wisata)
            if (matchScore > 0) {
                android.util.Log.d("SearchDebug", "Adding wisata: ${wisata.namaWisata} with score: $matchScore")
                val searchResult = SearchResult(
                    id = wisata.id,
                    name = wisata.namaWisata,
                    type = "Wisata",
                    rating = wisata.rating,
                    imageResource = wisata.imageResource,
                    wisataData = wisata
                )
                searchResults.add(Pair(searchResult, matchScore))
            } else {
                android.util.Log.d("SearchDebug", "Skipping wisata: ${wisata.namaWisata} (score: 0)")
            }
        }
        
        // Search in kuliner with strict filtering
        allKulinerList.forEach { kuliner ->
            val matchScore = calculateMatchScore(queryLower, kuliner)
            if (matchScore > 0) {
                android.util.Log.d("SearchDebug", "Adding kuliner: ${kuliner.namaKuliner} with score: $matchScore")
                val searchResult = SearchResult(
                    id = kuliner.id,
                    name = kuliner.namaKuliner,
                    type = "Kuliner",
                    rating = kuliner.rating,
                    imageResource = kuliner.imageResource,
                    kulinerData = kuliner
                )
                searchResults.add(Pair(searchResult, matchScore))
            } else {
                android.util.Log.d("SearchDebug", "Skipping kuliner: ${kuliner.namaKuliner} (score: 0)")
            }
        }
        
        // Sort by match score (descending) first, then by rating
        val sortedResults = searchResults
            .sortedWith(compareByDescending<Pair<SearchResult, Int>> { it.second }
                .thenByDescending { it.first.rating.toDoubleOrNull() ?: 0.0 })
            .take(8)
            .map { it.first } // Extract SearchResult from Pair
            
        android.util.Log.d("SearchDebug", "Total results found: ${sortedResults.size}")
        
        if (sortedResults.isNotEmpty()) {
            searchAdapter.updateResults(sortedResults)
            showSearchDropdown()
        } else {
            android.util.Log.d("SearchDebug", "No results found, hiding dropdown")
            hideSearchDropdown()
        }
    }
    
    private fun calculateMatchScore(query: String, wisata: Wisata): Int {
        var score = 0
        val nama = wisata.namaWisata.lowercase()
        val lokasi = wisata.lokasi.lowercase() 
        val kategori = wisata.kategori.lowercase()
        
        // Debug log
        android.util.Log.d("SearchDebug", "Checking wisata: ${wisata.namaWisata} with query: '$query'")
        
        // SUPER STRICT: ONLY exact character match from beginning
        // Check if name starts with exact query
        if (nama.startsWith(query)) {
            score += 100
            android.util.Log.d("SearchDebug", "✓ Name '${wisata.namaWisata}' starts with '$query': +100")
        }
        
        // Check if any word in name starts with exact query
        nama.split(" ").forEach { word ->
            if (word.isNotEmpty() && word.startsWith(query)) {
                score += 80
                android.util.Log.d("SearchDebug", "✓ Word '$word' starts with '$query': +80")
            }
        }
        
        // ONLY return score if there's a match - no partial matching allowed
        android.util.Log.d("SearchDebug", "Final score for ${wisata.namaWisata}: $score")
        return score
    }
    
    private fun calculateMatchScore(query: String, kuliner: Kuliner): Int {
        var score = 0
        val nama = kuliner.namaKuliner.lowercase()
        val lokasi = kuliner.lokasi.lowercase()
        val kategori = kuliner.kategori.lowercase()
        
        // Debug log
        android.util.Log.d("SearchDebug", "Checking kuliner: ${kuliner.namaKuliner} with query: '$query'")
        
        // SUPER STRICT: ONLY exact character match from beginning
        // Check if name starts with exact query
        if (nama.startsWith(query)) {
            score += 100
            android.util.Log.d("SearchDebug", "✓ Name '${kuliner.namaKuliner}' starts with '$query': +100")
        }
        
        // Check if any word in name starts with exact query
        nama.split(" ").forEach { word ->
            if (word.isNotEmpty() && word.startsWith(query)) {
                score += 80
                android.util.Log.d("SearchDebug", "✓ Word '$word' starts with '$query': +80")
            }
        }
        
        // ONLY return score if there's a match - no partial matching allowed
        android.util.Log.d("SearchDebug", "Final score for ${kuliner.namaKuliner}: $score")
        return score
    }
    
    private fun handleSearchItemClick(searchResult: SearchResult) {
        // Hide dropdown first
        hideSearchDropdown()
        
        // Clear search text and remove focus
        etSearch.setText("")
        etSearch.clearFocus()
        
        // Navigate to detail page
        when (searchResult.type) {
            "Wisata" -> {
                searchResult.wisataData?.let { wisata ->
                    openDetailWisata(wisata)
                }
            }
            "Kuliner" -> {
                searchResult.kulinerData?.let { kuliner ->
                    openDetailKuliner(kuliner)
                }
            }
        }
    }
    
    private fun showSearchDropdown() {
        positionDropdown() // Update position before showing
        searchDropdownContainer.visibility = View.VISIBLE
    }
    
    private fun hideSearchDropdown() {
        searchDropdownContainer.visibility = View.GONE
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
            val cardView = layoutInflater.inflate(R.layout.item_homepage_wisata_card, wisataContainer, false)
            
            // Set card data
            cardView.findViewById<ImageView>(R.id.img_homepage_wisata)?.setImageResource(wisata.imageResource)
            cardView.findViewById<TextView>(R.id.tv_homepage_wisata_nama)?.text = wisata.namaWisata
            cardView.findViewById<TextView>(R.id.tv_homepage_wisata_rating)?.text = wisata.rating
            
            // Set click listener for navigation
            cardView.setOnClickListener {
                openDetailWisata(wisata)
            }

            
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
            val cardView = layoutInflater.inflate(R.layout.item_homepage_kuliner_card, kulinerContainer, false)
            
            // Set card data
            cardView.findViewById<ImageView>(R.id.img_homepage_kuliner)?.setImageResource(kuliner.imageResource)
            cardView.findViewById<TextView>(R.id.tv_homepage_kuliner_nama)?.text = kuliner.namaKuliner
            cardView.findViewById<TextView>(R.id.tv_homepage_kuliner_rating)?.text = kuliner.rating
            
            // Set click listener for navigation
            cardView.setOnClickListener {
                openDetailKuliner(kuliner)
            }

            
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
        android.util.Log.d("HomepageActivity", "Opening detail for wisata: ${wisata.namaWisata} with ID: ${wisata.id}")
        val intent = Intent(this, DetailWisataActivity::class.java)
        intent.putExtra("wisata", wisata)
        startActivity(intent)
    }
    
    private fun openDetailKuliner(kuliner: Kuliner) {
        android.util.Log.d("HomepageActivity", "Opening detail for kuliner: ${kuliner.namaKuliner} with ID: ${kuliner.id}")
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
    
    private fun setupBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (searchDropdownContainer.visibility == View.VISIBLE) {
                    hideSearchDropdown()
                } else {
                    finish()
                }
            }
        })
    }
}
