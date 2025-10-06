package com.example.projectpemmob.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.projectpemmob.data.model.Wisata
import com.example.projectpemmob.data.repository.WisataRepository

object FavoritManager {
    private const val PREF_NAME = "favorit_wisata"
    private const val FAVORIT_LIST_KEY = "favorit_list"

    // Method untuk menambah wisata ke favorit menggunakan object Wisata
    fun addToFavorit(context: Context, wisata: Wisata) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf())?.toMutableSet() ?: mutableSetOf()

        // Menyimpan ID wisata untuk memudahkan pengelolaan
        favoritSet.add(wisata.id.toString())

        sharedPreferences.edit()
            .putStringSet(FAVORIT_LIST_KEY, favoritSet)
            .apply()
    }

    // Legacy method untuk backward compatibility
    fun addToFavorit(context: Context, namaWisata: String, rating: String, lokasi: String) {
        // Cari wisata berdasarkan nama dari repository
        val wisata = WisataRepository.getWisataByName(namaWisata)
        wisata?.let { addToFavorit(context, it) }
    }

    // New method to support HashMap data structure
    fun addFavorite(context: Context, data: HashMap<String, String>) {
        val namaWisata = data["nama_wisata"] ?: return
        val rating = data["rating"] ?: "0.0"
        val lokasi = data["lokasi"] ?: ""
        addToFavorit(context, namaWisata, rating, lokasi)
    }

    // Method untuk menghapus wisata dari favorit menggunakan object Wisata
    fun removeFromFavorit(context: Context, wisata: Wisata) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf())?.toMutableSet() ?: mutableSetOf()

        favoritSet.remove(wisata.id.toString())

        sharedPreferences.edit()
            .putStringSet(FAVORIT_LIST_KEY, favoritSet)
            .apply()
    }

    // Legacy method untuk backward compatibility
    fun removeFromFavorit(context: Context, namaWisata: String, rating: String, lokasi: String) {
        val wisata = WisataRepository.getWisataByName(namaWisata)
        wisata?.let { removeFromFavorit(context, it) }
    }

    // New method to remove favorite by name only
    fun removeFavorite(context: Context, namaWisata: String) {
        val wisata = WisataRepository.getWisataByName(namaWisata)
        wisata?.let { removeFromFavorit(context, it) }
    }

    // Method untuk mengecek apakah wisata ada di favorit menggunakan object Wisata
    fun isFavorit(context: Context, wisata: Wisata): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()

        return favoritSet.contains(wisata.id.toString())
    }

    // Legacy method untuk backward compatibility
    fun isFavorit(context: Context, namaWisata: String, rating: String, lokasi: String): Boolean {
        val wisata = WisataRepository.getWisataByName(namaWisata)
        return wisata?.let { isFavorit(context, it) } ?: false
    }

    // New method to check favorite by name only
    fun isFavorite(context: Context, namaWisata: String): Boolean {
        val wisata = WisataRepository.getWisataByName(namaWisata)
        return wisata?.let { isFavorit(context, it) } ?: false
    }

    // Method untuk mendapatkan daftar wisata favorit
    fun getFavoritWisataList(context: Context): List<Wisata> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()
        
        val favoritWisataList = mutableListOf<Wisata>()
        favoritSet.forEach { idString ->
            val id = idString.toIntOrNull()
            id?.let { 
                WisataRepository.getWisataById(it)?.let { wisata ->
                    favoritWisataList.add(wisata)
                }
            }
        }
        
        return favoritWisataList
    }

    // Legacy method untuk backward compatibility
    fun getFavoritList(context: Context): Set<String> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()
    }
}