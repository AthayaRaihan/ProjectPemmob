package com.example.projectpemmob.ui.profil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectpemmob.data.model.User
import com.example.projectpemmob.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    fun loadUser(uid: String) {
        userRepo.getUser(uid, {
            _user.postValue(it)
            _error.postValue(null)
        }, { e ->
            _user.postValue(null)
            _error.postValue(e)
        })
    }

    fun updateProfile(name: String, email: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _error.postValue(Exception("User not authenticated"))
            return
        }

        _isLoading.postValue(true)

        // Update Firebase Auth profile
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        currentUser.updateProfile(profileUpdates)
            .addOnSuccessListener {
                    updateFirestoreProfile(currentUser.uid, name, email)
            }
            .addOnFailureListener { e ->
                _isLoading.postValue(false)
                _error.postValue(e)
            }
    }

    private fun updateFirestoreProfile(uid: String, name: String, email: String) {
        userRepo.updateUserProfile(uid, name, email, {
            _isLoading.postValue(false)
            _updateSuccess.postValue(true)
            // Reload user data
            loadUser(uid)
        }, { e ->
            _isLoading.postValue(false)
            _error.postValue(e)
        })
    }

    fun clearMessages() {
        _error.postValue(null)
        _updateSuccess.postValue(false)
    }
}