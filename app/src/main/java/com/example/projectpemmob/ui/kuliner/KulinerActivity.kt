package com.example.projectpemmob.ui.kuliner

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.widget.addTextChangedListener
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Kuliner
import com.example.projectpemmob.data.repository.KulinerRepository
import com.example.projectpemmob.ui.detail.kuliner.DetailKulinerActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.profil.ProfileActivity
import com.example.projectpemmob.ui.wisata.WisataActivity
import com.example.projectpemmob.utils.FavoritManager

class KulinerActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var kulinerAdapter: KulinerAdapter
    private lateinit var searchEditText: EditText
    private lateinit var btnMie: TextView
    private lateinit var btnSate: TextView
    private lateinit var btnKripik: TextView
    private lateinit var btnLainnya: TextView
    
    private var allKulinerList: List<Kuliner> = emptyList()
    private var currentFilter: String = "Semua"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuliner)

        initViews()
        setupRecyclerView()
        loadKulinerData()
        setupSearch()
        setupFilterButtons()

        // Setup bottom navigation (shared handler)
        com.example.projectpemmob.ui.navigation.BottomNavigationHandler(this).setupBottomNavigation()
        // Ensure no transition animation so bottom nav appears static when returning
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        // Update heart icon states when returning to this activity
        kulinerAdapter.notifyDataSetChanged()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rv_kuliner)
        searchEditText = findViewById(R.id.et_search_kuliner)
        btnMie = findViewById(R.id.btn_mie)
        btnSate = findViewById(R.id.btn_sate)
        btnKripik = findViewById(R.id.btn_kripik)
        btnLainnya = findViewById(R.id.btn_lainnya)
    }
    
    private fun setupRecyclerView() {
        kulinerAdapter = KulinerAdapter(listOf(), this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = kulinerAdapter
    }
    
    private fun loadKulinerData() {
        allKulinerList = KulinerRepository.getAllKuliner()
        Log.d("KulinerActivity", "Loaded ${allKulinerList.size} kuliner items from repository")
        allKulinerList.forEach { kuliner ->
            Log.d("KulinerActivity", "Kuliner: ${kuliner.namaKuliner} - Rating: ${kuliner.rating} - Lokasi: ${kuliner.lokasi}")
        }
        filterKulinerByCategory(currentFilter)
    }
    
    private fun setupSearch() {
        searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim()
            if (query.isEmpty()) {
                filterKulinerByCategory(currentFilter)
            } else {
                val filteredList = KulinerRepository.searchKuliner(query)
                kulinerAdapter.updateData(filteredList)
            }
        }
    }
    
    private fun setupFilterButtons() {
        btnMie.setOnClickListener {
            selectFilter("Mie", btnMie)
        }
        
        btnSate.setOnClickListener {
            selectFilter("Sate", btnSate)
        }
        
        btnKripik.setOnClickListener {
            selectFilter("Kripik", btnKripik)
        }
        
        btnLainnya.setOnClickListener {
            selectFilter("Lainnya", btnLainnya)
        }
        
        // Set initial filter state - show all data by default
        resetFilterButtons()
        btnMie.setBackgroundResource(R.drawable.filter_button_selected)
        btnMie.setTextColor(resources.getColor(R.color.white, null))
        filterKulinerByCategory("Semua")
    }
    
    private fun selectFilter(filter: String, selectedButton: TextView) {
        currentFilter = filter
        
        // Reset all buttons
        resetFilterButtons()
        
        // Set selected button style
        selectedButton.setBackgroundResource(R.drawable.filter_button_selected)
        selectedButton.setTextColor(resources.getColor(R.color.white, null))
        
        // Filter data
        filterKulinerByCategory(filter)
    }
    
    private fun resetFilterButtons() {
        val buttons = listOf(btnMie, btnSate, btnKripik, btnLainnya)
        buttons.forEach { button ->
            button.setBackgroundResource(R.drawable.filter_button)
            button.setTextColor(resources.getColor(android.R.color.white, null))
        }
    }
    
    private fun filterKulinerByCategory(category: String) {
        val filteredList = when (category) {
            "Mie" -> allKulinerList.filter { 
                it.namaKuliner.contains("Mie", ignoreCase = true) 
            }
            "Sate" -> allKulinerList.filter { 
                it.namaKuliner.contains("Sate", ignoreCase = true) 
            }
            "Kripik" -> allKulinerList.filter { 
                it.namaKuliner.contains("Kripik", ignoreCase = true) ||
                it.namaKuliner.contains("Keripik", ignoreCase = true)
            }
            "Lainnya" -> allKulinerList.filter { 
                !it.namaKuliner.contains("Mie", ignoreCase = true) &&
                !it.namaKuliner.contains("Sate", ignoreCase = true) &&
                !it.namaKuliner.contains("Kripik", ignoreCase = true) &&
                !it.namaKuliner.contains("Keripik", ignoreCase = true)
            }
            else -> allKulinerList
        }
        
        kulinerAdapter.updateData(filteredList)
    }
}