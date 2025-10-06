package com.example.projectpemmob.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

object FavoritManager {
    private const val PREF_NAME = "favorit_wisata"
    private const val FAVORIT_LIST_KEY = "favorit_list"

    // In-memory cache for the currently loaded user's favorites (strings like "name|rating|lokasi")
    private var currentUserId: String? = null
    private val inMemoryFavorites: MutableSet<String> = mutableSetOf()

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun addToFavorit(context: Context, namaWisata: String, rating: String, lokasi: String) {
        val favoritItem = "$namaWisata|$rating|$lokasi"

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // update in-memory cache immediately
            inMemoryFavorites.add(favoritItem)
            currentUserId = user.uid
            // persist to Firestore array field users/{uid}.favorites
            try {
                db.collection("users").document(user.uid)
                    .set(mapOf("favorites" to FieldValue.arrayUnion(favoritItem)), com.google.firebase.firestore.SetOptions.merge())
            } catch (e: Exception) {
                Log.w("FavoritManager", "Failed to add favorite to Firestore: ${e.message}")
            }
            return
        }

        // Fallback: anonymous/local storage
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf())?.toMutableSet() ?: mutableSetOf()
        favoritSet.add(favoritItem)
        sharedPreferences.edit()
            .putStringSet(FAVORIT_LIST_KEY, favoritSet)
            .apply()
    }

    // New method to support HashMap data structure
    fun addFavorite(context: Context, data: HashMap<String, String>) {
        val namaWisata = data["nama_wisata"] ?: return
        val rating = data["rating"] ?: "0.0"
        val lokasi = data["lokasi"] ?: ""
        addToFavorit(context, namaWisata, rating, lokasi)
    }

    fun removeFromFavorit(context: Context, namaWisata: String, rating: String, lokasi: String) {
        val favoritItem = "$namaWisata|$rating|$lokasi"
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            inMemoryFavorites.remove(favoritItem)
            try {
                db.collection("users").document(user.uid)
                    .set(mapOf("favorites" to FieldValue.arrayRemove(favoritItem)), com.google.firebase.firestore.SetOptions.merge())
            } catch (e: Exception) {
                Log.w("FavoritManager", "Failed to remove favorite from Firestore: ${e.message}")
            }
            return
        }

        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf())?.toMutableSet() ?: mutableSetOf()
        favoritSet.remove(favoritItem)
        sharedPreferences.edit()
            .putStringSet(FAVORIT_LIST_KEY, favoritSet)
            .apply()
    }

    // New method to remove favorite by name only
    fun removeFavorite(context: Context, namaWisata: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val itemsToRemove = inMemoryFavorites.filter { it.startsWith("$namaWisata|") }
            itemsToRemove.forEach { item ->
                inMemoryFavorites.remove(item)
                try {
                    db.collection("users").document(user.uid)
                        .set(mapOf("favorites" to FieldValue.arrayRemove(item)), com.google.firebase.firestore.SetOptions.merge())
                } catch (e: Exception) {
                    Log.w("FavoritManager", "Failed to remove favorite (by name) from Firestore: ${e.message}")
                }
            }
            return
        }

        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf())?.toMutableSet() ?: mutableSetOf()
        val itemsToRemove = favoritSet.filter { it.startsWith("$namaWisata|") }
        itemsToRemove.forEach { favoritSet.remove(it) }
        sharedPreferences.edit()
            .putStringSet(FAVORIT_LIST_KEY, favoritSet)
            .apply()
    }

    fun isFavorit(context: Context, namaWisata: String, rating: String, lokasi: String): Boolean {
        val favoritItem = "$namaWisata|$rating|$lokasi"
        val user = FirebaseAuth.getInstance().currentUser
        return if (user != null && currentUserId == user.uid) {
            inMemoryFavorites.contains(favoritItem)
        } else {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()
            favoritSet.contains(favoritItem)
        }
    }

    // New method to check favorite by name only
    fun isFavorite(context: Context, namaWisata: String): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return if (user != null && currentUserId == user.uid) {
            inMemoryFavorites.any { it.startsWith("$namaWisata|") }
        } else {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val favoritSet = sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()
            favoritSet.any { it.startsWith("$namaWisata|") }
        }
    }

    fun getFavoritList(context: Context): Set<String> {
        val user = FirebaseAuth.getInstance().currentUser
        return if (user != null && currentUserId == user.uid) {
            inMemoryFavorites.toSet()
        } else {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getStringSet(FAVORIT_LIST_KEY, setOf()) ?: setOf()
        }
    }

    /**
     * Load favorites for the currently authenticated user into in-memory cache.
     * Calls onComplete after load (success or failure).
     */
    fun loadForCurrentUser(context: Context, onComplete: (() -> Unit)? = null) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // no user -> clear in-memory cache
            currentUserId = null
            inMemoryFavorites.clear()
            onComplete?.invoke()
            return
        }

        val uid = user.uid
        if (currentUserId == uid) {
            // already loaded
            onComplete?.invoke()
            return
        }

        // load favorites array from users/{uid} document
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val favList = doc.get("favorites") as? List<*>
                inMemoryFavorites.clear()
                if (favList != null) {
                    for (item in favList) {
                        if (item is String) inMemoryFavorites.add(item)
                    }
                }
                currentUserId = uid
                onComplete?.invoke()
            }
            .addOnFailureListener { e ->
                Log.w("FavoritManager", "Failed to load favorites for user $uid: ${e.message}")
                currentUserId = uid
                inMemoryFavorites.clear()
                onComplete?.invoke()
            }
    }
}