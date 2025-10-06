package com.example.projectpemmob.data.repository

import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Kuliner

object KulinerRepository {
    
    private val kulinerList = listOf(
        Kuliner(
            id = 1,
            namaKuliner = "Mie Ongklok",
            rating = "4.7",
            lokasi = "Wonosobo, Jawa Tengah",
            description = "Mie ongklok adalah makanan khas Wonosobo yang terbuat dari mie lebar dengan kuah kaldu yang gurih. Disajikan dengan tauge, daun kol, dan irisan daging sapi yang empuk. Rasanya yang unik dengan bumbu rempah khas Jawa membuat mie ongklok menjadi primadona kuliner Wonosobo.",
            imageResource = R.drawable.ongklok1,
            latitude = -7.3669,
            longitude = 109.9003,
            harga = "Rp 15.000 - 25.000",
            jamBuka = "06.00 - 22.00 WIB",
            kategori = "Makanan Utama",
        ),
        Kuliner(
            id = 2,
            namaKuliner = "Tempe Kemul",
            rating = "4.5",
            lokasi = "Wonosobo, Jawa Tengah",
            description = "Tempe kemul adalah makanan tradisional khas Wonosobo yang terbuat dari tempe yang dibungkus dengan daun pisang kemudian direbus dengan bumbu rempah. Teksturnya yang lembut dan rasa yang gurih membuat tempe kemul menjadi camilan favorit masyarakat lokal.",
            imageResource = R.drawable.kemul1,
            latitude = -7.3650,
            longitude = 109.9020,
            harga = "Rp 8.000 - 12.000",
            jamBuka = "07.00 - 17.00 WIB",
            kategori = "Camilan Tradisional",
        ),
        Kuliner(
            id = 3,
            namaKuliner = "Carica",
            rating = "4.6",
            lokasi = "Dieng, Wonosobo",
            description = "Carica adalah buah khas dataran tinggi Dieng yang diolah menjadi berbagai produk seperti manisan, sirup, dan selai. Buah carica memiliki rasa segar dengan kandungan vitamin C yang tinggi. Produk olahan carica menjadi oleh-oleh wajib dari Wonosobo.",
            imageResource = R.drawable.carica1,
            latitude = -7.2075,
            longitude = 109.9225,
            harga = "Rp 20.000 - 50.000",
            jamBuka = "08.00 - 20.00 WIB",
            kategori = "Buah & Olahan",
        ),
        Kuliner(
            id = 4,
            namaKuliner = "Sego Megono",
            rating = "4.4",
            lokasi = "Wonosobo, Jawa Tengah",
            description = "Sego megono adalah nasi yang disajikan dengan lauk pauk sederhana namun kaya rasa. Biasanya disajikan dengan sayur lodeh, tempe goreng, tahu, dan sambal. Hidangan ini mencerminkan kesederhanaan masyarakat Wonosobo namun kaya akan cita rasa.",
            imageResource = R.drawable.megono1,
            latitude = -7.3600,
            longitude = 109.8950,
            harga = "Rp 10.000 - 18.000",
            jamBuka = "06.00 - 14.00 WIB",
            kategori = "Makanan Utama",
        ),
        Kuliner(
            id = 5,
            namaKuliner = "Geblek",
            rating = "4.3",
            lokasi = "Wonosobo, Jawa Tengah",
            description = "Geblek adalah makanan tradisional yang terbuat dari singkong yang diparut kemudian dibentuk bulat dan direbus. Disajikan dengan kelapa parut dan gula merah. Teksturnya yang kenyal dan rasa manis alami membuat geblek menjadi camilan favorit di Wonosobo.",
            imageResource = R.drawable.geblek1,
            latitude = -7.3580,
            longitude = 109.8980,
            harga = "Rp 5.000 - 10.000",
            jamBuka = "08.00 - 18.00 WIB",
            kategori = "Camilan Tradisional",
        )
    )

    fun getAllKuliner(): List<Kuliner> {
        android.util.Log.d("KulinerRepository", "Returning ${kulinerList.size} kuliner items")
        return kulinerList
    }

    fun getKulinerById(id: Int): Kuliner? {
        android.util.Log.d("KulinerRepository", "Looking for kuliner with ID: $id")
        val result = kulinerList.find { it.id == id }
        android.util.Log.d("KulinerRepository", "Found kuliner: ${result?.namaKuliner ?: "null"}")
        return result
    }

    fun getKulinerByName(name: String): Kuliner? {
        android.util.Log.d("KulinerRepository", "Looking for kuliner with name: $name")
        val result = kulinerList.find { it.namaKuliner.equals(name, ignoreCase = true) }
        android.util.Log.d("KulinerRepository", "Found kuliner: ${result?.namaKuliner ?: "null"}")
        return result
    }

    fun searchKuliner(query: String): List<Kuliner> {
        android.util.Log.d("KulinerRepository", "Searching kuliner with query: $query")
        val result = kulinerList.filter { 
            it.namaKuliner.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true) ||
            it.kategori.contains(query, ignoreCase = true) ||
            it.lokasi.contains(query, ignoreCase = true)
        }
        android.util.Log.d("KulinerRepository", "Found ${result.size} kuliner items")
        return result
    }
}