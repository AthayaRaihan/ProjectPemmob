package com.example.projectpemmob.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.projectpemmob.R
import com.example.projectpemmob.data.repository.WisataRepository
import com.example.projectpemmob.data.repository.KulinerRepository
import com.example.projectpemmob.ui.detail.kuliner.DetailKulinerActivity
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.wisata.WisataActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.projectpemmob.utils.FavoritManager

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
                loadHomeContent() // Load home content first so we can find the TextView
                
                // Find TextView in the loaded home content
                contentLayout.findViewById<TextView>(R.id.tvGreetingName)?.let { tvGreetingName ->
                    // Ambil user yang login
                    val user = FirebaseAuth.getInstance().currentUser
                    
                    // Get display name from current user, fallback to "User" if not logged in or no display name
                    val displayName = when {
                        user?.displayName != null && user.displayName!!.isNotEmpty() -> user.displayName
                        user?.email != null -> user.email?.substringBefore("@") // Gunakan email sebagai fallback
                        else -> "User"
                    }
                    
                    tvGreetingName.text = "Hai $displayName,"
                }
                
                setupCardClickListeners()
                setupDynamicHomepageContent()

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
                
                // Update greeting text when loading home content
                homeView.findViewById<TextView>(R.id.tvGreetingName)?.let { tvGreetingName ->
                    val user = FirebaseAuth.getInstance().currentUser
                    val displayName = when {
                        user?.displayName != null && user.displayName!!.isNotEmpty() -> user.displayName
                        user?.email != null -> user.email?.substringBefore("@")
                        else -> "User"
                    }
                    tvGreetingName.text = "Hai $displayName,"
                }
                
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
                val favoritesView =
                    inflater.inflate(R.layout.activity_favorit, contentLayout, false)
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
                    // Card Wisata click listeners di homepage - gunakan ID yang benar
                    contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_homepage_dieng_1)
                        ?.setOnClickListener {
                            openDetailWisata(7) // Dieng Plateau
                        }

                    contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_homepage_dieng_2)
                        ?.setOnClickListener {
                            openDetailWisata(6) // Kawah Sikidang
                        }

                    contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_homepage_telaga_warna)
                        ?.setOnClickListener {
                            openDetailWisata(5) // Telaga Warna
                        }

                    contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_homepage_arjuna)
                        ?.setOnClickListener {
                            openDetailWisata(8) // Candi Arjuna
                        }

                    // Quick action cards
                    contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_dieng_1)
                        ?.setOnClickListener {
                            loadTourismContent()
                            updateBottomNavigation("tourism")
                        }

                    contentLayout.findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok)
                        ?.setOnClickListener {
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
                    // Setup favorite button untuk Dieng Plateau di homepage
                    contentLayout.findViewById<ImageView>(R.id.heart_homepage_dieng_1)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(7) // Dieng Plateau
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }

                    // Setup favorite button untuk Kawah Sikidang di homepage
                    contentLayout.findViewById<ImageView>(R.id.heart_gunung_prau)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(6) // Kawah Sikidang
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }

                    // Setup favorite button untuk Telaga Warna di homepage
                    contentLayout.findViewById<ImageView>(R.id.heart_telaga_warna)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(5) // Telaga Warna
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }

                    // Setup favorite button untuk Candi Arjuna di homepage
                    contentLayout.findViewById<ImageView>(R.id.heart_homepage_arjuna)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(8) // Candi Arjuna
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            private fun openDetailWisata(wisataId: Int) {
                val intent = Intent(this, DetailWisataActivity::class.java)
                intent.putExtra(DetailWisataActivity.EXTRA_WISATA_ID, wisataId)
                startActivity(intent)
            }

            // Legacy method untuk backward compatibility
            private fun openDetailWisata(namaWisata: String, rating: String, lokasi: String) {
                val wisata = WisataRepository.getWisataByName(namaWisata)
                wisata?.let { openDetailWisata(it.id) }
            }

            private fun setupDynamicHomepageContent() {
                try {
                    // Update wisata cards dengan data dari repository
                    updateWisataCard(R.id.card_homepage_dieng_1, 7) // Dieng Plateau
                    updateWisataCard(R.id.card_homepage_dieng_2, 6) // Kawah Sikidang  
                    updateWisataCard(R.id.card_homepage_telaga_warna, 5) // Telaga Warna
                    updateWisataCard(R.id.card_homepage_arjuna, 8) // Candi Arjuna

                    // Update kuliner card dengan data dari repository
                    updateKulinerCard(0, 1) // Mie Ongklok - gunakan index karena tidak ada ID khusus
                } catch (e: Exception) {
                    android.util.Log.e("HomepageActivity", "Error setting up dynamic content", e)
                }
            }

            private fun updateWisataCard(cardId: Int, wisataId: Int) {
                try {
                    val wisata = WisataRepository.getWisataById(wisataId)
                    wisata?.let { w ->
                        val card = contentLayout.findViewById<CardView>(cardId)
                        card?.let { cardView ->
                            // Update text dan gambar menggunakan traverse method
                            updateTextInCard(cardView, w.namaWisata, w.rating)
                            updateImageInCard(cardView, w.imageResource)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("HomepageActivity", "Error updating wisata card $cardId", e)
                }
            }

            private fun updateTextInCard(cardView: CardView, namaWisata: String, rating: String) {
                try {
                    // Traverse semua TextView dalam card untuk menemukan yang tepat
                    val textViews = getAllTextViews(cardView)
                    textViews.forEachIndexed { index, textView ->
                        when {
                            textView.text.toString().contains("Dieng") || 
                            textView.text.toString().contains("Kawah") ||
                            textView.text.toString().contains("Telaga") ||
                            textView.text.toString().contains("Candi") -> {
                                textView.text = namaWisata
                            }
                            textView.text.toString().matches(Regex("\\d\\.\\d")) -> {
                                textView.text = rating
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("HomepageActivity", "Error updating text in card", e)
                }
            }

            private fun updateImageInCard(cardView: CardView, imageResource: Int) {
                try {
                    val imageViews = getAllImageViews(cardView)
                    // Update ImageView utama (biasanya yang pertama dan bukan heart icon)
                    imageViews.find { it.id != R.id.heart_homepage_dieng_1 && 
                                     it.id != R.id.heart_gunung_prau &&
                                     it.id != R.id.heart_telaga_warna &&
                                     it.id != R.id.heart_homepage_arjuna &&
                                     it.scaleType == ImageView.ScaleType.CENTER_CROP }
                        ?.setImageResource(imageResource)
                } catch (e: Exception) {
                    android.util.Log.e("HomepageActivity", "Error updating image in card", e)
                }
            }

            private fun getAllTextViews(view: View): List<TextView> {
                val textViews = mutableListOf<TextView>()
                if (view is TextView) {
                    textViews.add(view)
                } else if (view is ViewGroup) {
                    for (i in 0 until view.childCount) {
                        textViews.addAll(getAllTextViews(view.getChildAt(i)))
                    }
                }
                return textViews
            }

            private fun getAllImageViews(view: View): List<ImageView> {
                val imageViews = mutableListOf<ImageView>()
                if (view is ImageView) {
                    imageViews.add(view)
                } else if (view is ViewGroup) {
                    for (i in 0 until view.childCount) {
                        imageViews.addAll(getAllImageViews(view.getChildAt(i)))
                    }
                }
                return imageViews
            }

            private fun updateKulinerCard(cardId: Int, kulinerId: Int) {
                try {
                    val kuliner = KulinerRepository.getKulinerById(kulinerId)
                    kuliner?.let { k ->
                        // Untuk kuliner, kita akan mencari card kuliner berdasarkan text content
                        val kulinerCards = findKulinerCards()
                        kulinerCards.firstOrNull()?.let { cardView ->
                            // Update text dalam card kuliner
                            updateKulinerTextInCard(cardView, k.namaKuliner, k.rating)
                            
                            // Update gambar kuliner
                            updateKulinerImageInCard(cardView, k.imageResource)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("HomepageActivity", "Error updating kuliner card $cardId", e)
                }
            }

            private fun findKulinerCards(): List<CardView> {
                val kulinerCards = mutableListOf<CardView>()
                try {
                    // Cari semua CardView yang mengandung text "Mie Ongklok" atau kuliner lainnya
                    val allCardViews = getAllCardViews(contentLayout)
                    allCardViews.forEach { cardView ->
                        val textViews = getAllTextViews(cardView)
                        if (textViews.any { it.text.toString().contains("Mie", ignoreCase = true) ||
                                           it.text.toString().contains("Kuliner", ignoreCase = true) }) {
                            kulinerCards.add(cardView)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("HomepageActivity", "Error finding kuliner cards", e)
                }
                return kulinerCards
            }

            private fun getAllCardViews(view: View): List<CardView> {
                val cardViews = mutableListOf<CardView>()
                if (view is CardView) {
                    cardViews.add(view)
                } else if (view is ViewGroup) {
                    for (i in 0 until view.childCount) {
                        cardViews.addAll(getAllCardViews(view.getChildAt(i)))
                    }
                }
                return cardViews
            }

            private fun updateKulinerTextInCard(cardView: CardView, namaKuliner: String, rating: String) {
                try {
                    val textViews = getAllTextViews(cardView)
                    textViews.forEach { textView ->
                        when {
                            textView.text.toString().contains("Mie", ignoreCase = true) -> {
                                textView.text = namaKuliner
                            }
                            textView.text.toString().matches(Regex("\\d\\.\\d")) -> {
                                textView.text = rating
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("HomepageActivity", "Error updating kuliner text", e)
                }
            }

            private fun updateKulinerImageInCard(cardView: CardView, imageResource: Int) {
                try {
                    val imageViews = getAllImageViews(cardView)
                    // Update ImageView utama untuk kuliner (yang menggunakan culinary_noodles)
                    imageViews.find { it.scaleType == ImageView.ScaleType.CENTER_CROP && 
                                     it.drawable != null }
                        ?.setImageResource(imageResource)
                } catch (e: Exception) {
                    android.util.Log.e("HomepageActivity", "Error updating kuliner image", e)
                }
            }

            private fun setupTourismClickListeners(tourismView: android.view.View) {
                try {
                    // Setup click listeners untuk cards di halaman wisata - gunakan ID yang benar
                    tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_dieng_1)
                        ?.setOnClickListener {
                            openDetailWisata(7) // Dieng Plateau
                        }

                    tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_sikidang)
                        ?.setOnClickListener {
                            openDetailWisata(6) // Kawah Sikidang
                        }

                    tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_telaga_warna)
                        ?.setOnClickListener {
                            openDetailWisata(5) //
                        }

                    tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_telaga_menjer)
                        ?.setOnClickListener {
                            openDetailWisata(1) // Telaga Menjer
                        }

                    tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_bukit_sikunir)
                        ?.setOnClickListener {
                            openDetailWisata(2) // Bukit Sikunir
                        }

                    tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_gunung_prau)
                        ?.setOnClickListener {
                            openDetailWisata(3) // Gunung Prau
                        }

                    tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_bukit_scooter)
                        ?.setOnClickListener {
                            openDetailWisata(4) // Bukit Scooter
                        }

                    tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_gunung_prau)
                        ?.setOnClickListener {
                            openDetailWisata(3) // Gunung Prau
                        }

                    tourismView.findViewById<androidx.cardview.widget.CardView>(R.id.card_arjuna)
                        ?.setOnClickListener {
                            openDetailWisata(8) // Gunung Prau
                        }

                    // Setup favorite buttons untuk wisata
                    setupWisataFavoriteButtons(tourismView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            private fun setupCulinaryClickListeners(culinaryView: android.view.View) {
                try {
                    // Setup click listeners untuk 5 kuliner dinamis
                    culinaryView.findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok)
                        ?.setOnClickListener {
                            openDetailKuliner(1) // Mie Ongklok
                        }

                    culinaryView.findViewById<androidx.cardview.widget.CardView>(R.id.card_mie_ongklok_2)
                        ?.setOnClickListener {
                            openDetailKuliner(2) // Tempe Kemul
                        }

                    culinaryView.findViewById<androidx.cardview.widget.CardView>(R.id.card_carica)
                        ?.setOnClickListener {
                            openDetailKuliner(3) // Carica
                        }

                    culinaryView.findViewById<androidx.cardview.widget.CardView>(R.id.card_sego_megono)
                        ?.setOnClickListener {
                            openDetailKuliner(4) // Sego Megono
                        }

                    culinaryView.findViewById<androidx.cardview.widget.CardView>(R.id.card_geblek)
                        ?.setOnClickListener {
                            openDetailKuliner(5) // Geblek
                        }
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
                val emptyStateLayout =
                    favoritesView.findViewById<LinearLayout>(R.id.empty_state_layout)
                val favoritListLayout =
                    favoritesView.findViewById<LinearLayout>(R.id.favorit_list_layout)

                val favoritWisataList = FavoritManager.getFavoritWisataList(this)

                if (favoritWisataList.isEmpty()) {
                    // Show empty state
                    emptyStateLayout?.visibility = View.VISIBLE
                    favoritListLayout?.visibility = View.GONE
                } else {
                    // Show favorit list
                    emptyStateLayout?.visibility = View.GONE
                    favoritListLayout?.visibility = View.VISIBLE
                    populateFavoritList(favoritWisataList, favoritListLayout)
                }
            }

            private fun populateFavoritList(
                favoritWisataList: List<com.example.projectpemmob.data.model.Wisata>,
                favoritListLayout: LinearLayout?
            ) {
                favoritListLayout?.removeAllViews()

                for (wisata in favoritWisataList) {
                    addFavoritCard(wisata, favoritListLayout)
                }
            }

            private fun addFavoritCard(
                wisata: com.example.projectpemmob.data.model.Wisata,
                favoritListLayout: LinearLayout?
            ) {
                try {
                    val cardView = layoutInflater.inflate(
                        R.layout.item_favorit_card,
                        favoritListLayout,
                        false
                    ) as androidx.cardview.widget.CardView

                    // Set data to card
                    cardView.findViewById<android.widget.TextView>(R.id.tv_nama_wisata_favorit)?.text =
                        wisata.namaWisata
                    cardView.findViewById<android.widget.TextView>(R.id.tv_rating_favorit)?.text =
                        wisata.rating

                    // Add click listener to open detail
                    cardView.setOnClickListener {
                        openDetailWisata(wisata.id)
                    }

                    // Add remove from favorit functionality
                    cardView.findViewById<View>(R.id.btn_remove_favorit)?.setOnClickListener {
                        removeFromFavoritAndRefresh(wisata)
                    }

                    favoritListLayout?.addView(cardView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            private fun removeFromFavoritAndRefresh(wisata: com.example.projectpemmob.data.model.Wisata) {
                FavoritManager.removeFromFavorit(this, wisata)

                // Refresh favorit content if currently showing favorites
                if (currentPage == "favorites") {
                    loadFavoritesContent()
                }

                Toast.makeText(this, "${wisata.namaWisata} dihapus dari favorit", Toast.LENGTH_SHORT).show()
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

            private fun setupWisataFavoriteButtons(tourismView: android.view.View) {
                try {
                    // Setup favorite button untuk Dieng Plateau
                    tourismView.findViewById<ImageView>(R.id.heart_dieng_1)?.let { favoriteButton ->
                        val wisata = WisataRepository.getWisataById(7) // Dieng Plateau
                        wisata?.let {
                            updateFavoriteButtonState(favoriteButton, it)
                            favoriteButton.setOnClickListener {
                                toggleFavorite(favoriteButton, wisata)
                            }
                        }
                    }

                    // Setup favorite button untuk Kawah Sikidang
                    tourismView.findViewById<ImageView>(R.id.heart_sikidang)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(6) // Kawah Sikidang
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }

                    // Setup favorite button untuk Candi Arjuna (card_telaga_warna)
                    tourismView.findViewById<ImageView>(R.id.heart_telaga_warna)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(5) // Candi Arjuna
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }

                    // Setup favorite button untuk Telaga Menjer
                    tourismView.findViewById<ImageView>(R.id.heart_telaga_menjer)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(1) // Telaga Menjer
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }

                    // Setup favorite button untuk Bukit Sikunir
                    tourismView.findViewById<ImageView>(R.id.heart_bukit_sikunir)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(2) // Bukit Sikunir
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }

                    // Setup favorite button untuk Gunung Prau
                    tourismView.findViewById<ImageView>(R.id.heart_gunung_prau)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(3) // Gunung Prau
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }

                    // Setup favorite button untuk Bukit Scooter
                    tourismView.findViewById<ImageView>(R.id.heart_bukit_scooter)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(4) // Bukit Scooter
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }

                    tourismView.findViewById<ImageView>(R.id.heart_arjuna)
                        ?.let { favoriteButton ->
                            val wisata = WisataRepository.getWisataById(8) // Gunung Prau
                            wisata?.let {
                                updateFavoriteButtonState(favoriteButton, it)
                                favoriteButton.setOnClickListener {
                                    toggleFavorite(favoriteButton, wisata)
                                }
                            }
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            private fun updateFavoriteButtonState(
                favoriteButton: ImageView,
                wisata: com.example.projectpemmob.data.model.Wisata
            ) {
                val isFavorite = FavoritManager.isFavorit(this, wisata)
                favoriteButton.setImageResource(
                    if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorites
                )
            }

            // Legacy method untuk backward compatibility
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
                wisata: com.example.projectpemmob.data.model.Wisata
            ) {
                val isFavorite = FavoritManager.isFavorit(this, wisata)

                if (isFavorite) {
                    FavoritManager.removeFromFavorit(this, wisata)
                    favoriteButton.setImageResource(R.drawable.ic_favorites)
                    Toast.makeText(this, "${wisata.namaWisata} dihapus dari favorit", Toast.LENGTH_SHORT).show()
                } else {
                    FavoritManager.addToFavorit(this, wisata)
                    favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
                    Toast.makeText(this, "${wisata.namaWisata} ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                }
            }

            // Legacy method untuk backward compatibility
            private fun toggleFavorite(
                favoriteButton: ImageView,
                nama: String,
                rating: String,
                lokasi: String,
                tipe: String
            ) {
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
