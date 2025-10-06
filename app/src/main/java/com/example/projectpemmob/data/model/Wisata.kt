package com.example.projectpemmob.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Wisata(
    val id: Int,
    val namaWisata: String,
    val rating: String,
    val lokasi: String,
    val description: String,
    val imageResource: Int,
    val latitude: Double,
    val longitude: Double,
    val kategori: String = "wisata",
    val harga: String = "Gratis",
    val jamBuka: String = "24 Jam",
) : Parcelable