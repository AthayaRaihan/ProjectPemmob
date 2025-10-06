package com.example.projectpemmob.ui.adapter

import android.content.Context
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

class WisataAdapter(
    private val context: Context,
    private var wisataList: List<Wisata>
) : RecyclerView.Adapter<WisataAdapter.WisataViewHolder>() {

    class WisataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardWisata: CardView = itemView.findViewById(R.id.card_wisata)
        val imgWisata: ImageView = itemView.findViewById(R.id.img_wisata)
        val tvNamaWisata: TextView = itemView.findViewById(R.id.tv_nama_wisata)
        val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        val tvLokasi: TextView = itemView.findViewById(R.id.tv_lokasi)
        val btnHeart: ImageView = itemView.findViewById(R.id.btn_heart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WisataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wisata_card, parent, false)
        return WisataViewHolder(view)
    }

    override fun onBindViewHolder(holder: WisataViewHolder, position: Int) {
        val wisata = wisataList[position]

        // Set data to views
        holder.imgWisata.setImageResource(wisata.imageResource)
        holder.tvNamaWisata.text = wisata.namaWisata
        holder.tvRating.text = wisata.rating
        holder.tvLokasi.text = wisata.lokasi

        // Set heart button state
        updateHeartButton(holder.btnHeart, wisata)

        // Set card click listener
        holder.cardWisata.setOnClickListener {
            val intent = Intent(context, DetailWisataActivity::class.java)
            intent.putExtra(DetailWisataActivity.EXTRA_WISATA, wisata)
            context.startActivity(intent)
        }

        // Set heart button click listener
        holder.btnHeart.setOnClickListener {
            toggleFavorit(holder.btnHeart, wisata)
        }
    }

    override fun getItemCount(): Int = wisataList.size

    private fun updateHeartButton(heartButton: ImageView, wisata: Wisata) {
        if (FavoritManager.isFavorit(context, wisata)) {
            heartButton.setImageResource(R.drawable.ic_heart_filled)
        } else {
            heartButton.setImageResource(R.drawable.ic_heart)
        }
    }

    private fun toggleFavorit(heartButton: ImageView, wisata: Wisata) {
        if (FavoritManager.isFavorit(context, wisata)) {
            FavoritManager.removeFromFavorit(context, wisata)
            heartButton.setImageResource(R.drawable.ic_heart)
        } else {
            FavoritManager.addToFavorit(context, wisata)
            heartButton.setImageResource(R.drawable.ic_heart_filled)
        }
    }

    fun updateData(newWisataList: List<Wisata>) {
        wisataList = newWisataList
        notifyDataSetChanged()
    }

    fun refreshFavoriteStates() {
        notifyDataSetChanged()
    }
}