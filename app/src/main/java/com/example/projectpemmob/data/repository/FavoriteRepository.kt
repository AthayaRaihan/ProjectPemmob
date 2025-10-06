package com.example.projectpemmob.data.repository

import com.example.projectpemmob.data.model.FavoriteItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class FavoriteRepository {
    private val db = FirebaseFirestore.getInstance()

    fun loadFavorites(onSuccess: (List<FavoriteItem>) -> Unit, onError: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onSuccess(emptyList())
            return
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val favList = doc.get("favorites") as? List<*>
                val result = mutableListOf<FavoriteItem>()
                if (favList != null) {
                    for (raw in favList) {
                        if (raw is String) {
                            val parts = raw.split("|")
                            if (parts.size >= 3) {
                                result.add(FavoriteItem(parts[0], parts[1], parts[2]))
                            }
                        }
                    }
                }
                onSuccess(result)
            }
            .addOnFailureListener { e ->
                Log.w("FavoriteRepository", "Failed to load favorites: ${e.message}")
                onError(e)
            }
    }

    fun addFavorite(item: FavoriteItem, onComplete: (() -> Unit)? = null, onError: ((Exception) -> Unit)? = null) {
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            onError?.invoke(Exception("No user"))
            return
        }
        val raw = "${item.nama}|${item.rating}|${item.lokasi}"
        db.collection("users").document(user.uid)
            .set(mapOf("favorites" to com.google.firebase.firestore.FieldValue.arrayUnion(raw)), com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener { onComplete?.invoke() }
            .addOnFailureListener { e -> onError?.invoke(e) }
    }

    fun removeFavorite(item: FavoriteItem, onComplete: (() -> Unit)? = null, onError: ((Exception) -> Unit)? = null) {
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            onError?.invoke(Exception("No user"))
            return
        }
        val raw = "${item.nama}|${item.rating}|${item.lokasi}"
        db.collection("users").document(user.uid)
            .set(mapOf("favorites" to com.google.firebase.firestore.FieldValue.arrayRemove(raw)), com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener { onComplete?.invoke() }
            .addOnFailureListener { e -> onError?.invoke(e) }
    }
}
