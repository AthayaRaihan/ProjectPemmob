package com.example.projectpemmob.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.SearchResult

class SearchDropdownAdapter(
    private val onItemClick: (SearchResult) -> Unit
) : RecyclerView.Adapter<SearchDropdownAdapter.SearchViewHolder>() {

    private var searchResults: List<SearchResult> = emptyList()

    fun updateResults(results: List<SearchResult>) {
        searchResults = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_dropdown, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(searchResults[position])
    }

    override fun getItemCount(): Int = searchResults.size

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgItem: ImageView = itemView.findViewById(R.id.img_search_item)
        private val tvName: TextView = itemView.findViewById(R.id.tv_search_item_name)
        private val tvType: TextView = itemView.findViewById(R.id.tv_search_item_type)
        private val tvRating: TextView = itemView.findViewById(R.id.tv_search_item_rating)

        fun bind(searchResult: SearchResult) {
            imgItem.setImageResource(searchResult.imageResource)
            tvName.text = searchResult.name
            tvType.text = searchResult.type
            tvRating.text = searchResult.rating

            itemView.setOnClickListener {
                onItemClick(searchResult)
            }
        }
    }
}