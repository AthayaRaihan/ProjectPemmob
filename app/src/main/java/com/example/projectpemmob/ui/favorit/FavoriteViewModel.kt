package com.example.projectpemmob.ui.favorit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectpemmob.data.model.FavoriteItem
import com.example.projectpemmob.data.repository.FavoriteRepository

class FavoriteViewModel : ViewModel() {
    private val repo = FavoriteRepository()

    private val _favorites = MutableLiveData<List<FavoriteItem>>(emptyList())
    val favorites: LiveData<List<FavoriteItem>> = _favorites

    private val _error = MutableLiveData<Exception?>(null)
    val error: LiveData<Exception?> = _error

    fun loadFavorites() {
        repo.loadFavorites({ list ->
            _favorites.postValue(list)
            _error.postValue(null)
        }, { e ->
            _favorites.postValue(emptyList())
            _error.postValue(e)
        })
    }

    fun addFavorite(item: FavoriteItem) {
        repo.addFavorite(item, { loadFavorites() }, { e -> _error.postValue(e) })
    }

    fun removeFavorite(item: FavoriteItem) {
        repo.removeFavorite(item, { loadFavorites() }, { e -> _error.postValue(e) })
    }
}
