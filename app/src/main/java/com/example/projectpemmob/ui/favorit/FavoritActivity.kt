package com.example.projectpemmob.ui.favorit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.projectpemmob.R
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.wisata.WisataActivity
import com.example.projectpemmob.utils.FavoritManager
import androidx.activity.viewModels
import com.example.projectpemmob.data.model.FavoriteItem
import com.example.projectpemmob.ui.favorit.FavoriteViewModel
import com.example.projectpemmob.ui.profil.ProfileActivity

class FavoritActivity : AppCompatActivity() {

    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var favoritListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorit)

        // Initialize views
        emptyStateLayout = findViewById(R.id.empty_state_layout)
        favoritListLayout = findViewById(R.id.favorit_list_layout)

    // Setup bottom navigation (shared handler)
    com.example.projectpemmob.ui.navigation.BottomNavigationHandler(this).setupBottomNavigation()
    // Ensure no transition animation so bottom nav appears static when returning
    overridePendingTransition(0, 0)

        // Use ViewModel to load and observe favorites
        val viewModel: FavoriteViewModel by viewModels()
        viewModel.favorites.observe(this) { list ->
            if (list.isEmpty()) {
                emptyStateLayout.visibility = View.VISIBLE
                favoritListLayout.visibility = View.GONE
            } else {
                emptyStateLayout.visibility = View.GONE
                favoritListLayout.visibility = View.VISIBLE
                populateFavoritListFromModels(list, viewModel)
            }
        }

        viewModel.error.observe(this) { e ->
            // Could show a toast/snackbar. For now, just log and show empty state.
            if (e != null) {
                // keep empty state visible if error
            }
        }

        // initial load
        viewModel.loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        // Refresh via ViewModel
        val viewModel: FavoriteViewModel by viewModels()
        viewModel.loadFavorites()
    }

    private fun populateFavoritListFromModels(list: List<FavoriteItem>, viewModel: FavoriteViewModel) {
        favoritListLayout.removeAllViews()

        for (item in list) {
            addFavoritCardModel(item, viewModel)
        }
    }

    private fun addFavoritCardModel(item: FavoriteItem, viewModel: FavoriteViewModel) {
        val cardView = layoutInflater.inflate(R.layout.item_favorit_card, favoritListLayout, false) as CardView

        // Set data to card
        cardView.findViewById<TextView>(R.id.tv_nama_wisata_favorit).text = item.nama
        cardView.findViewById<TextView>(R.id.tv_rating_favorit).text = item.rating

        // Add click listener to open detail
        cardView.setOnClickListener {
            openDetailWisata(item.nama, item.rating, item.lokasi)
        }

        // Add remove from favorit functionality via ViewModel
        cardView.findViewById<View>(R.id.btn_remove_favorit).setOnClickListener {
            viewModel.removeFavorite(item)
        }

        favoritListLayout.addView(cardView)
    }

    

    private fun openDetailWisata(namaWisata: String, rating: String, lokasi: String) {
        val intent = Intent(this, DetailWisataActivity::class.java)
        intent.putExtra("nama_wisata", namaWisata)
        intent.putExtra("rating", rating)
        intent.putExtra("lokasi", lokasi)
        startActivity(intent)
    }

    // Navigation handled by BottomNavigationHandler
}