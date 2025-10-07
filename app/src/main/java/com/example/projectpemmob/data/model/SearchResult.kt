package com.example.projectpemmob.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResult(
    val id: Int,
    val name: String,
    val type: String, // "Wisata" atau "Kuliner"
    val rating: String,
    val imageResource: Int,
    val wisataData: Wisata? = null,
    val kulinerData: Kuliner? = null
) : Parcelable