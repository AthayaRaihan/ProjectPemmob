# Refactoring Project Pemmob - Sistem Wisata Dinamis

## Ringkasan Perubahan

Project ini telah di-refactor dari sistem statis menjadi sistem dinamis untuk pengelolaan data wisata. Sekarang aplikasi menggunakan satu layout detail wisata yang dapat menampilkan berbagai wisata hanya dengan memanggil data dari repository.

## Struktur Baru

### 1. Model Data (`Wisata.kt`)
```kotlin
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
    val fasilitas: List<String> = emptyList()
)
```

### 2. Repository (`WisataRepository.kt`)
- Menyimpan semua data wisata dalam satu tempat
- Menyediakan method untuk mengambil data berdasarkan ID, nama, atau pencarian
- Saat ini memiliki 5 data wisata: Dieng Plateau, Kawah Sikidang, Telaga Warna, Candi Arjuna, Golden Sunrise Sikunir

### 3. FavoritManager (Diperbaharui)
- Sekarang menggunakan ID wisata untuk manajemen favorit yang lebih efisien
- Kompatibel dengan sistem lama
- Menyediakan method baru yang menggunakan object Wisata

### 4. DetailWisataActivity (Diperbaharui)
- Mendukung 3 cara untuk menerima data:
  1. Object Wisata langsung (EXTRA_WISATA)
  2. ID Wisata (EXTRA_WISATA_ID)
  3. Legacy method dengan string terpisah (backward compatibility)
- Menampilkan data secara dinamis termasuk harga, jam buka, dan fasilitas

### 5. WisataActivity (Diperbaharui)
- Menggunakan data dari WisataRepository
- Mengirim data dengan ID wisata ke DetailWisataActivity
- Tetap kompatibel dengan layout existing

## Fitur Baru

### 1. Data Dinamis
- Semua wisata sekarang memiliki data lengkap (koordinat GPS, harga, jam buka, fasilitas)
- Mudah menambah wisata baru di repository

### 2. Sistem Favorit yang Diperbaharui
- Menggunakan ID untuk tracking yang lebih akurat
- Backward compatibility dengan sistem lama

### 3. Layout Detail yang Fleksibel
- Satu layout dapat menampilkan semua wisata
- Menampilkan informasi dinamis (harga, jam buka, fasilitas)
- Koordinat GPS yang akurat untuk setiap wisata

### 4. Adapter untuk RecyclerView (Siap Digunakan)
- `WisataAdapter.kt` dan `item_wisata_card.xml` telah disiapkan
- Dapat digunakan untuk implementasi RecyclerView di masa depan

## Cara Menambah Wisata Baru

1. Buka `WisataRepository.kt`
2. Tambahkan data wisata baru di dalam `wisataList`
3. Pastikan image resource tersedia di drawable
4. Update layout activity_wisata.xml jika perlu menambah card baru

Contoh:
```kotlin
Wisata(
    id = 6,
    namaWisata = "Nama Wisata Baru",
    rating = "4.5",
    lokasi = "Lokasi, Wonosobo, Jawa Tengah",
    description = "Deskripsi lengkap wisata...",
    imageResource = R.drawable.image_wisata_baru,
    latitude = -7.xxxx,
    longitude = 109.xxxx,
    harga = "Rp 10.000",
    jamBuka = "08.00 - 17.00 WIB",
    fasilitas = listOf("Toilet", "Parkir", "Warung")
)
```

## Cara Penggunaan

### Membuka Detail Wisata
```kotlin
// Cara 1: Menggunakan object Wisata
val intent = Intent(this, DetailWisataActivity::class.java)
intent.putExtra(DetailWisataActivity.EXTRA_WISATA, wisataObject)
startActivity(intent)

// Cara 2: Menggunakan ID
val intent = Intent(this, DetailWisataActivity::class.java)
intent.putExtra(DetailWisataActivity.EXTRA_WISATA_ID, wisataId)
startActivity(intent)
```

### Mengelola Favorit
```kotlin
// Cek apakah favorit
val isFavorite = FavoritManager.isFavorit(context, wisataObject)

// Tambah ke favorit
FavoritManager.addToFavorit(context, wisataObject)

// Hapus dari favorit
FavoritManager.removeFromFavorit(context, wisataObject)

// Dapatkan list favorit
val favoritList = FavoritManager.getFavoritWisataList(context)
```

## Keuntungan Sistem Baru

1. **Maintainability**: Lebih mudah maintain dan update data
2. **Scalability**: Mudah menambah wisata baru tanpa membuat activity baru
3. **Consistency**: Semua wisata menggunakan layout dan behavior yang sama
4. **Efficiency**: Mengurangi duplikasi code
5. **Data Integrity**: Sistem favorit yang lebih akurat dengan ID

## File yang Diubah

1. `/data/model/Wisata.kt` (BARU)
2. `/data/repository/WisataRepository.kt` (BARU)
3. `/ui/adapter/WisataAdapter.kt` (BARU)
4. `/layout/item_wisata_card.xml` (BARU)
5. `/utils/FavoritManager.kt` (DIPERBAHARUI)
6. `/ui/detail/wisata/DetailWisataActivity.kt` (DIPERBAHARUI)
7. `/ui/wisata/WisataActivity.kt` (DIPERBAHARUI)
8. `/layout/activity_detail_wisata.xml` (DIPERBAHARUI - tambah ID untuk harga, jam buka, fasilitas)
9. `/build.gradle.kts` (DIPERBAHARUI - tambah kotlin-parcelize)

## Testing

Untuk testing aplikasi:

1. Pastikan semua import berhasil
2. Build project untuk memeriksa error
3. Test navigasi dari WisataActivity ke DetailWisataActivity
4. Test fungsi favorit (tambah/hapus)
5. Test pembukaan Maps dengan koordinat yang benar
6. Verifikasi data ditampilkan dengan benar (nama, rating, lokasi, deskripsi, harga, jam buka)

## Langkah Selanjutnya (Opsional)

1. Implementasi RecyclerView di WisataActivity menggunakan WisataAdapter
2. Tambah fitur search wisata
3. Integrasi dengan database lokal (Room) atau remote (Firebase)
4. Tambah fitur filter berdasarkan kategori, harga, atau rating
5. Implementasi paging untuk data yang banyak