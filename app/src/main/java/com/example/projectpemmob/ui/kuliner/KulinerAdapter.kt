package com.example.projectpemmob.ui.kuliner

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Kuliner
import com.example.projectpemmob.ui.detail.kuliner.DetailKulinerActivity
import com.example.projectpemmob.utils.FavoritManager
import com.google.firebase.auth.FirebaseAuth

class KulinerAdapter(
    private var kulinerList: List<Kuliner>,
    private val context: Context
) : RecyclerView.Adapter<KulinerAdapter.KulinerViewHolder>() {

    class KulinerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivKuliner: ImageView = itemView.findViewById(R.id.iv_kuliner)
        val tvNamaKuliner: TextView = itemView.findViewById(R.id.tv_nama_kuliner)
        val tvRating: TextView = itemView.findViewById(R.id.tv_rating)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KulinerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kuliner_card, parent, false)
        return KulinerViewHolder(view)
    }

    override fun onBindViewHolder(holder: KulinerViewHolder, position: Int) {
        val kuliner = kulinerList[position]

        // Set data
        holder.tvNamaKuliner.text = kuliner.namaKuliner
        holder.tvRating.text = kuliner.rating
        
        // Set image from kuliner data
        holder.ivKuliner.setImageResource(kuliner.imageResource)
        
        // Set heart icon state

        
        // Card click listener
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailKulinerActivity::class.java)
            intent.putExtra("kuliner", kuliner)
            context.startActivity(intent)
        }
        
      
    }

    override fun getItemCount(): Int = kulinerList.size

    fun updateData(newKulinerList: List<Kuliner>) {
        kulinerList = newKulinerList
        notifyDataSetChanged()
    }

    private fun updateHeartIcon(heartIcon: ImageView, kuliner: Kuliner) {
        if (FavoritManager.isFavorit(context, kuliner.namaKuliner, kuliner.rating, kuliner.lokasi)) {
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        } else {
            heartIcon.setImageResource(R.drawable.ic_heart)
        }
    }

    private fun toggleFavorit(heartIcon: ImageView, kuliner: Kuliner) {
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

        if (FavoritManager.isFavorit(context, kuliner.namaKuliner, kuliner.rating, kuliner.lokasi)) {
            // Remove from favorit
            FavoritManager.removeFromFavorit(context, kuliner.namaKuliner, kuliner.rating, kuliner.lokasi)
            heartIcon.setImageResource(R.drawable.ic_heart)
        } else {
            // Add to favorit
            FavoritManager.addToFavorit(context, kuliner.namaKuliner, kuliner.rating, kuliner.lokasi)
            heartIcon.setImageResource(R.drawable.ic_heart_filled)
        }
    }
}