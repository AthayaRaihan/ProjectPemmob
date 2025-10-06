package com.example.projectpemmob.ui.wisata.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.utils.FavoritManager
import com.google.firebase.auth.FirebaseAuth

class WisataAdapter(
    private var wisataList: List<Wisata>,
    private val context: android.content.Context
) : RecyclerView.Adapter<WisataAdapter.WisataViewHolder>() {

    class WisataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_wisata)
        val imageWisata: ImageView = itemView.findViewById(R.id.img_wisata)
        val tvNamaWisata: TextView = itemView.findViewById(R.id.tv_nama_wisata)
        val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        val tvLokasi: TextView = itemView.findViewById(R.id.tv_lokasi)
        val heartIcon: ImageView = itemView.findViewById(R.id.heart_wisata)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WisataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wisata_card, parent, false)
        return WisataViewHolder(view)
    }

    override fun onBindViewHolder(holder: WisataViewHolder, position: Int) {
        val wisata = wisataList[position]

        // Set data to views
        holder.imageWisata.setImageResource(wisata.imageResource)
        holder.tvNamaWisata.text = wisata.namaWisata
        holder.tvRating.text = wisata.rating
        holder.tvLokasi.text = wisata.lokasi

        // Set favorite icon state
        updateHeartIcon(holder.heartIcon, wisata)

        // Set card click listener
        holder.cardView.setOnClickListener {
            val intent = Intent(context, DetailWisataActivity::class.java)
            intent.putExtra("wisata_data", wisata)
            context.startActivity(intent)
        }

        // Set heart icon click listener
        holder.heartIcon.setOnClickListener {
            toggleFavorit(holder.heartIcon, wisata)
        }
    }

    override fun getItemCount(): Int = wisataList.size

    fun updateData(newWisataList: List<Wisata>) {
        wisataList = newWisataList
        notifyDataSetChanged()
    }

    private fun updateHeartIcon(heartIcon: ImageView, wisata: Wisata) {
        if (FavoritManager.isFavorit(context, wisata.namaWisata, wisata.rating, wisata.lokasi)) {
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        } else {
            heartIcon.setImageResource(R.drawable.ic_heart)
        }
    }

    private fun toggleFavorit(heartIcon: ImageView, wisata: Wisata) {
        // Require login before allowing favorit changes
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Perlu Login")
                .setMessage("Anda harus login jika ingin menambahkan favorit.")
                .setPositiveButton("OK") { _, _ ->
                    val intent = Intent(context, com.example.projectpemmob.ui.auth.LoginActivity::class.java)
                    context.startActivity(intent)
                }
                .setNegativeButton("Batal", null)
                .show()
            return
        }

        if (FavoritManager.isFavorit(context, wisata.namaWisata, wisata.rating, wisata.lokasi)) {
            // Remove from favorit
            FavoritManager.removeFromFavorit(context, wisata.namaWisata, wisata.rating, wisata.lokasi)
            heartIcon.setImageResource(R.drawable.ic_heart)
        } else {
            // Add to favorit
            FavoritManager.addToFavorit(context, wisata.namaWisata, wisata.rating, wisata.lokasi)
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        }
    }
}