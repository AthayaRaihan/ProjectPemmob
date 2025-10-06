package com.example.projectpemmob.ui.profil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectpemmob.data.model.User
import com.example.projectpemmob.data.repository.UserRepository

class ProfileViewModel : ViewModel() {
    private val userRepo = UserRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> = _error

    fun loadUser(uid: String) {
        userRepo.getUser(uid, {
            _user.postValue(it)
            _error.postValue(null)
        }, { e ->
            _user.postValue(null)
            _error.postValue(e)
        })
    }
}