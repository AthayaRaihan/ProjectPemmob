package com.example.projectpemmob.data.repository

import com.example.projectpemmob.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
	private val db = FirebaseFirestore.getInstance()

	/**
	 * Fetches a user document from Firestore `users/{uid}` and returns a User via callbacks.
	 */
	fun getUser(uid: String, onSuccess: (User?) -> Unit, onError: (Exception) -> Unit) {
		db.collection("users").document(uid).get()
			.addOnSuccessListener { doc ->
				if (doc != null && doc.exists()) {
					val user = User(
						uid = uid,
						name = doc.getString("name"),
						email = doc.getString("email"),
						photoUrl = doc.getString("photoUrl")
					)
					onSuccess(user)
				} else {
					onSuccess(null)
				}
			}
			.addOnFailureListener { e ->
				onError(e)
			}
	}

	/**
	 * Update the user's name field in Firestore.
	 */
	fun updateName(uid: String, name: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
		db.collection("users").document(uid)
			.update("name", name)
			.addOnSuccessListener { onSuccess() }
			.addOnFailureListener { e -> onError(e) }
	}
}