package com.example.projectpemmob

import android.content.Context
import android.content.SharedPreferences

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
    
    fun removeFromFavorit(context: Context, namaWisata: String, rating: String, lokasi: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf())?.toMutableSet() ?: mutableSetOf()
        
        val favoritItem = "$namaWisata|$rating|$lokasi"
        favoritSet.remove(favoritItem)
        
        sharedPreferences.edit()
            .putStringSet(FAVORIT_LIST_KEY, favoritSet)
            .apply()
    }
    
    fun isFavorit(context: Context, namaWisata: String, rating: String, lokasi: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()
        
        val favoritItem = "$namaWisata|$rating|$lokasi"
        return favoritSet.contains(favoritItem)
    }
    
    fun getFavoritList(context: Context): Set<String> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()
    }
}