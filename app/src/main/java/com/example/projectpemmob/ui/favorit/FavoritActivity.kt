package com.example.projectpemmob.ui.favorit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpemmob.R
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.wisata.WisataActivity
import com.example.projectpemmob.utils.FavoritManager
import androidx.activity.viewModels
import com.example.projectpemmob.data.model.FavoriteItem
import com.example.projectpemmob.ui.favorit.FavoriteViewModel
import com.example.projectpemmob.ui.favorit.adapter.FavoritAdapter
import com.example.projectpemmob.ui.profil.ProfileActivity

class FavoritActivity : AppCompatActivity() {

    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritAdapter: FavoritAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorit)

        // Initialize views
        emptyStateLayout = findViewById(R.id.empty_state_layout)
        recyclerView = findViewById(R.id.rv_favorit)
        
        // Setup RecyclerView
        setupRecyclerView()

        // Setup bottom navigation (shared handler)
        com.example.projectpemmob.ui.navigation.BottomNavigationHandler(this).setupBottomNavigation()
        // Ensure no transition animation so bottom nav appears static when returning
        overridePendingTransition(0, 0)

        // Use ViewModel to load and observe favorites
        val viewModel: FavoriteViewModel by viewModels()
        viewModel.favorites.observe(this) { list ->
            if (list.isEmpty()) {
                emptyStateLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyStateLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                favoritAdapter.updateData(list)
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
    
    private fun setupRecyclerView() {
        favoritAdapter = FavoritAdapter(listOf(), this) { favoriteItem ->
            // Handle remove favorit
            val viewModel: FavoriteViewModel by viewModels()
            viewModel.removeFavorite(favoriteItem)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = favoritAdapter
    }

    override fun onResume() {
        super.onResume()
        // Refresh via ViewModel
        val viewModel: FavoriteViewModel by viewModels()
        viewModel.loadFavorites()
    }
}