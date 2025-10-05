package com.example.projectpemmob.utils

import android.content.Context

object FavoritManager {
    private const val PREF_NAME = "favorit_wisata"
    private const val FAVORIT_LIST_KEY = "favorit_list"

    fun addToFavorit(context: Context, namaWisata: String, rating: String, lokasi: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf())?.toMutableSet() ?: mutableSetOf()

        val favoritItem = "$namaWisata|$rating|$lokasi"
        favoritSet.add(favoritItem)

        sharedPreferences.edit()
            .putStringSet(FAVORIT_LIST_KEY, favoritSet)
            .apply()
    }
<<<<<<< HEAD:app/src/main/java/com/example/projectpemmob/utils/FavoritManager.kt

=======
    
    // New method to support HashMap data structure
    fun addFavorite(context: Context, data: HashMap<String, String>) {
        val namaWisata = data["nama_wisata"] ?: return
        val rating = data["rating"] ?: "0.0"
        val lokasi = data["lokasi"] ?: ""
        addToFavorit(context, namaWisata, rating, lokasi)
    }
    
>>>>>>> 2c8a7973da8f909b8d065fa96852fc84994a7bf0:app/src/main/java/com/example/projectpemmob/FavoritManager.kt
    fun removeFromFavorit(context: Context, namaWisata: String, rating: String, lokasi: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf())?.toMutableSet() ?: mutableSetOf()

        val favoritItem = "$namaWisata|$rating|$lokasi"
        favoritSet.remove(favoritItem)

        sharedPreferences.edit()
            .putStringSet(FAVORIT_LIST_KEY, favoritSet)
            .apply()
    }
<<<<<<< HEAD:app/src/main/java/com/example/projectpemmob/utils/FavoritManager.kt

=======
    
    // New method to remove favorite by name only
    fun removeFavorite(context: Context, namaWisata: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf())?.toMutableSet() ?: mutableSetOf()
        
        // Remove any item that starts with the wisata name
        val itemsToRemove = favoritSet.filter { it.startsWith("$namaWisata|") }
        itemsToRemove.forEach { favoritSet.remove(it) }
        
        sharedPreferences.edit()
            .putStringSet(FAVORIT_LIST_KEY, favoritSet)
            .apply()
    }
    
>>>>>>> 2c8a7973da8f909b8d065fa96852fc84994a7bf0:app/src/main/java/com/example/projectpemmob/FavoritManager.kt
    fun isFavorit(context: Context, namaWisata: String, rating: String, lokasi: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()

        val favoritItem = "$namaWisata|$rating|$lokasi"
        return favoritSet.contains(favoritItem)
    }
<<<<<<< HEAD:app/src/main/java/com/example/projectpemmob/utils/FavoritManager.kt

=======
    
    // New method to check favorite by name only
    fun isFavorite(context: Context, namaWisata: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()
        
        return favoritSet.any { it.startsWith("$namaWisata|") }
    }
    
>>>>>>> 2c8a7973da8f909b8d065fa96852fc84994a7bf0:app/src/main/java/com/example/projectpemmob/FavoritManager.kt
    fun getFavoritList(context: Context): Set<String> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()
    }
}