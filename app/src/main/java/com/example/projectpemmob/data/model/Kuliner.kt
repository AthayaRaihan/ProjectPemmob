package com.example.projectpemmob.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kuliner(
    val id: Int,
    val namaKuliner: String,
    val rating: String,
    val lokasi: String,
    val description: String,
    val imageResource: Int,
    val latitude: Double,
    val longitude: Double,
    val harga: String,
    val jamBuka: String,
    val kategori: String,
) : Parcelable