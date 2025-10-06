package com.example.projectpemmob.ui.favorit.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.FavoriteItem
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.data.model.Kuliner
import com.example.projectpemmob.data.repository.WisataRepository
import com.example.projectpemmob.data.repository.KulinerRepository
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.ui.detail.kuliner.DetailKulinerActivity
import com.example.projectpemmob.utils.FavoritManager
import android.util.Log

class FavoritAdapter(
    private var favoritList: List<FavoriteItem>,
    private val context: Context,
    private val onRemoveFavorit: (FavoriteItem) -> Unit
) : RecyclerView.Adapter<FavoritAdapter.FavoritViewHolder>() {

    class FavoritViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFavorit: ImageView = itemView.findViewById(R.id.iv_favorit)
        val tvNamaFavorit: TextView = itemView.findViewById(R.id.tv_nama_favorit)
        val tvRatingFavorit: TextView = itemView.findViewById(R.id.tv_rating_favorit)
        val tvLokasieFavorit: TextView = itemView.findViewById(R.id.tv_lokasi_favorit)
        val btnRemoveFavorit: ImageView = itemView.findViewById(R.id.btn_remove_favorit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorit_card, parent, false)
        return FavoritViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritViewHolder, position: Int) {
        val favorit = favoritList[position]
        Log.d("FavoritAdapter", "Binding favorit: ${favorit.nama}")
        
        // Try to find matching data in repositories
        val wisataData = WisataRepository.getAllWisata().find { 
            it.namaWisata.equals(favorit.nama, ignoreCase = true) 
        }
        val kulinerData = KulinerRepository.getAllKuliner().find { 
            it.namaKuliner.equals(favorit.nama, ignoreCase = true) 
        }

        // Set data based on what was found
        when {
            wisataData != null -> {
                Log.d("FavoritAdapter", "Found wisata data for: ${favorit.nama}")
                setupWisataData(holder, wisataData)
                setupWisataClickListener(holder, wisataData)
            }
            kulinerData != null -> {
                Log.d("FavoritAdapter", "Found kuliner data for: ${favorit.nama}")
                setupKulinerData(holder, kulinerData)
                setupKulinerClickListener(holder, kulinerData)
            }
            else -> {
                Log.d("FavoritAdapter", "Using fallback data for: ${favorit.nama}")
                // Fallback for items not found in repository
                setupFallbackData(holder, favorit)
                setupFallbackClickListener(holder, favorit)
            }
        }

        // Remove favorit listener
        holder.btnRemoveFavorit.setOnClickListener {
            onRemoveFavorit(favorit)
        }
    }

    override fun getItemCount(): Int = favoritList.size

    fun updateData(newFavoritList: List<FavoriteItem>) {
        favoritList = newFavoritList
        Log.d("FavoritAdapter", "Updated favorit list with ${favoritList.size} items")
        notifyDataSetChanged()
    }

    private fun setupWisataData(holder: FavoritViewHolder, wisata: Wisata) {
        holder.tvNamaFavorit.text = wisata.namaWisata
        holder.tvRatingFavorit.text = wisata.rating
        holder.tvLokasieFavorit.text = wisata.lokasi
        holder.ivFavorit.setImageResource(wisata.imageResource)
    }

    private fun setupKulinerData(holder: FavoritViewHolder, kuliner: Kuliner) {
        holder.tvNamaFavorit.text = kuliner.namaKuliner
        holder.tvRatingFavorit.text = kuliner.rating
        holder.tvLokasieFavorit.text = kuliner.lokasi
        holder.ivFavorit.setImageResource(kuliner.imageResource)
    }

    private fun setupFallbackData(holder: FavoritViewHolder, favorit: FavoriteItem) {
        holder.tvNamaFavorit.text = favorit.nama
        holder.tvRatingFavorit.text = favorit.rating
        holder.tvLokasieFavorit.text = favorit.lokasi
        holder.ivFavorit.setImageResource(R.drawable.background_login) // default image
    }

    private fun setupWisataClickListener(holder: FavoritViewHolder, wisata: Wisata) {
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailWisataActivity::class.java)
            intent.putExtra("wisata_data", wisata)
            context.startActivity(intent)
        }
    }

    private fun setupKulinerClickListener(holder: FavoritViewHolder, kuliner: Kuliner) {
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailKulinerActivity::class.java)
            intent.putExtra("kuliner", kuliner)
            context.startActivity(intent)
        }
    }

    private fun setupFallbackClickListener(holder: FavoritViewHolder, favorit: FavoriteItem) {
        holder.itemView.setOnClickListener {
            // Try to determine if it's wisata or kuliner based on name and open appropriate detail
            val wisataData = WisataRepository.getAllWisata().find { 
                it.namaWisata.contains(favorit.nama, ignoreCase = true) 
            }
            if (wisataData != null) {
                val intent = Intent(context, DetailWisataActivity::class.java)
                intent.putExtra("wisata_data", wisataData)
                context.startActivity(intent)
            } else {
                // Default to legacy wisata detail
                val intent = Intent(context, DetailWisataActivity::class.java)
                intent.putExtra("nama_wisata", favorit.nama)
                intent.putExtra("rating", favorit.rating)
                intent.putExtra("lokasi", favorit.lokasi)
                context.startActivity(intent)
            }
        }
    }
}