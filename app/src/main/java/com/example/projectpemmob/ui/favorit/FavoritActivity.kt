package com.example.projectpemmob.ui.favorit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.utils.FavoritManager

class FavoritActivity : AppCompatActivity() {
    
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var favoritListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorit)

        try {
            emptyStateLayout = findViewById(R.id.empty_state_layout)
            favoritListLayout = findViewById(R.id.favorit_list_layout)
            
            loadFavoritData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavoritData()
    }

    private fun loadFavoritData() {
        try {
            val favoritWisataList = FavoritManager.getFavoritWisataList(this)

            if (favoritWisataList.isEmpty()) {
                showEmptyState()
            } else {
                showFavoritList(favoritWisataList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showEmptyState()
        }
    }

    private fun showEmptyState() {
        try {
            emptyStateLayout.visibility = View.VISIBLE
            favoritListLayout.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showFavoritList(favoritWisataList: List<Wisata>) {
        try {
            emptyStateLayout.visibility = View.GONE
            favoritListLayout.visibility = View.VISIBLE
            favoritListLayout.removeAllViews()

            for (wisata in favoritWisataList) {
                addFavoritItemToLayout(wisata)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showEmptyState()
        }
    }

    private fun addFavoritItemToLayout(wisata: Wisata) {
        try {
            val cardView = layoutInflater.inflate(R.layout.item_favorit_card, null) as CardView

            // Set data wisata dengan ID yang benar sesuai layout
            val namaTextView = cardView.findViewById<TextView>(R.id.tv_nama_wisata_favorit)
            val ratingTextView = cardView.findViewById<TextView>(R.id.tv_rating_favorit)
            val lokasiTextView = cardView.findViewById<TextView>(R.id.tv_lokasi_favorit)
            val imageView = cardView.findViewById<ImageView>(R.id.iv_wisata_favorit)
            val heartIcon = cardView.findViewById<ImageView>(R.id.btn_remove_favorit)

            // Set data dengan null safety
            namaTextView?.text = wisata.namaWisata
            ratingTextView?.text = wisata.rating
            lokasiTextView?.text = wisata.lokasi
            imageView?.setImageResource(wisata.imageResource)

            // Set heart icon untuk favorit
            heartIcon?.setImageResource(R.drawable.ic_heart_filled)

            // Click listener untuk card
            cardView.setOnClickListener {
                navigateToDetail(wisata)
            }

            // Click listener untuk heart icon (remove dari favorit)
            heartIcon?.setOnClickListener {
                FavoritManager.removeFromFavorit(this@FavoritActivity, wisata)
                Toast.makeText(this@FavoritActivity, "${wisata.namaWisata} dihapus dari favorit", Toast.LENGTH_SHORT).show()
                loadFavoritData() // Refresh list
            }

            favoritListLayout.addView(cardView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToDetail(wisata: Wisata) {
        try {
            val intent = Intent(this, DetailWisataActivity::class.java)
            // Gunakan ID untuk navigasi yang lebih reliable
            intent.putExtra(DetailWisataActivity.EXTRA_WISATA_ID, wisata.id)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}