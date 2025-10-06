package com.example.projectpemmob.ui.wisata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.data.repository.WisataRepository
import com.example.projectpemmob.ui.detail.wisata.DetailWisataActivity
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.wisata.adapter.WisataAdapter
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.profil.ProfileActivity
import com.example.projectpemmob.utils.FavoritManager

class WisataActivity : AppCompatActivity() {
    
    private lateinit var wisataAdapter: WisataAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private var allWisataList: List<Wisata> = listOf()
    
    // Filter buttons
    private lateinit var btnBukit: TextView
    private lateinit var btnTelaga: TextView
    private lateinit var btnCurug: TextView
    private lateinit var btnAgrowisata: TextView
    
    private var currentFilter = "Semua" // Default filter untuk menampilkan semua data
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wisata)

        // Setup bottom navigation (shared handler)
        com.example.projectpemmob.ui.navigation.BottomNavigationHandler(this).setupBottomNavigation()
        // Ensure no transition animation so bottom nav appears static when returning
        overridePendingTransition(0, 0)

        // Initialize views
        initViews()
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Load data from repository
        loadWisataData()

        // Setup search functionality
        setupSearch()
        
        // Setup filter buttons
        setupFilterButtons()

        // Test repository data
        testWisataRepository()

        // Setup heart icon listeners after loading per-user favorites
        FavoritManager.loadForCurrentUser(this) {
            // Refresh adapter to update heart icons
            wisataAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        // Update heart icon states when returning to this activity
        wisataAdapter.notifyDataSetChanged()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rv_wisata)
        searchEditText = findViewById(R.id.et_search_wisata)
        btnBukit = findViewById(R.id.btn_bukit)
        btnTelaga = findViewById(R.id.btn_telaga)
        btnCurug = findViewById(R.id.btn_curug)
        btnAgrowisata = findViewById(R.id.btn_agrowisata)
    }
    
    private fun setupRecyclerView() {
        wisataAdapter = WisataAdapter(listOf(), this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = wisataAdapter
    }
    
    private fun loadWisataData() {
        allWisataList = WisataRepository.getAllWisata()
        Log.d("WisataActivity", "Loaded ${allWisataList.size} wisata items from repository")
        allWisataList.forEach { wisata ->
            Log.d("WisataActivity", "Wisata: ${wisata.namaWisata} - Rating: ${wisata.rating} - Lokasi: ${wisata.lokasi}")
        }
        filterWisataByCategory(currentFilter)
    }
    
    private fun setupSearch() {
        searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim()
            if (query.isEmpty()) {
                filterWisataByCategory(currentFilter)
            } else {
                val filteredList = WisataRepository.searchWisata(query)
                wisataAdapter.updateData(filteredList)
            }
        }
    }
    
    private fun setupFilterButtons() {
        btnBukit.setOnClickListener {
            selectFilter("Bukit", btnBukit)
        }
        
        btnTelaga.setOnClickListener {
            selectFilter("Telaga", btnTelaga)
        }
        
        btnCurug.setOnClickListener {
            selectFilter("Curug", btnCurug)
        }
        
        btnAgrowisata.setOnClickListener {
            selectFilter("Agrowisata", btnAgrowisata)
        }
        
        // Set initial filter state - show all data by default
        resetFilterButtons()
        btnBukit.setBackgroundResource(R.drawable.filter_button_selected)
        btnBukit.setTextColor(resources.getColor(R.color.white, null))
        filterWisataByCategory("Semua")
    }
    
    private fun selectFilter(filter: String, selectedButton: TextView) {
        currentFilter = filter
        
        // Reset all buttons
        resetFilterButtons()
        
        // Set selected button style
        selectedButton.setBackgroundResource(R.drawable.filter_button_selected)
        selectedButton.setTextColor(resources.getColor(R.color.white, null))
        
        // Filter data
        filterWisataByCategory(filter)
    }
    
    private fun resetFilterButtons() {
        val buttons = listOf(btnBukit, btnTelaga, btnCurug, btnAgrowisata)
        buttons.forEach { button ->
            button.setBackgroundResource(R.drawable.filter_button)
            button.setTextColor(resources.getColor(android.R.color.white, null))
        }
    }
    
    private fun filterWisataByCategory(category: String) {
        val filteredList = when (category) {
            "Bukit" -> allWisataList.filter { 
                it.namaWisata.contains("Bukit", ignoreCase = true) ||
                it.namaWisata.contains("Gunung", ignoreCase = true)
            }
            "Telaga" -> allWisataList.filter { 
                it.namaWisata.contains("Telaga", ignoreCase = true) ||
                it.namaWisata.contains("Danau", ignoreCase = true)
            }
            "Curug" -> allWisataList.filter { 
                it.namaWisata.contains("Curug", ignoreCase = true) ||
                it.namaWisata.contains("Air Terjun", ignoreCase = true)
            }
            "Agrowisata" -> allWisataList.filter { 
                it.namaWisata.contains("Agro", ignoreCase = true) ||
                it.kategori.contains("Agrowisata", ignoreCase = true)
            }
            else -> allWisataList
        }
        
        wisataAdapter.updateData(filteredList)
    }

    private fun testWisataRepository() {
        // Test repository untuk memastikan data bisa diakses
        val allWisata = WisataRepository.getAllWisata()
        Log.d("WisataActivity", "Total wisata: ${allWisata.size}")
        
        allWisata.forEach { wisata ->
            Log.d("WisataActivity", "Wisata: ${wisata.namaWisata} - Rating: ${wisata.rating}")
        }
        
        // Test pencarian berdasarkan ID
        val wisataById = WisataRepository.getWisataById(1)
        Log.d("WisataActivity", "Wisata dengan ID 1: ${wisataById?.namaWisata}")
        
        // Test pencarian berdasarkan nama
        val wisataByName = WisataRepository.getWisataByName("Dieng Plateau")
        Log.d("WisataActivity", "Wisata dengan nama 'Dieng Plateau': ${wisataByName?.namaWisata}")
    }
}