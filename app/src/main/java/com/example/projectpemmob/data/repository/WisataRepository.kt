package com.example.projectpemmob.data.repository

import com.example.projectpemmob.R
import com.example.projectpemmob.data.model.Wisata

object WisataRepository {
    
    private val wisataList = listOf(
        Wisata(
            id = 1,
            namaWisata = "Telaga Menjer",
            rating = "4.6",
            lokasi = "Garung, Wonosobo, Jawa Tengah",
            description = "Telaga Menjer merupakan danau buatan terbesar di Wonosobo yang dikelilingi oleh perbukitan hijau yang menawan. Danau ini terbentuk dari bendungan yang dibangun untuk irigasi dan pembangkit listrik. Dengan luas sekitar 70 hektar, Telaga Menjer menawarkan pemandangan yang sangat indah terutama saat sunrise dan sunset.\n\nDi sekitar telaga terdapat berbagai aktivitas menarik seperti berperahu, memancing, atau sekadar menikmati pemandangan dari gazebo yang tersedia. Air danau yang jernih dengan latar belakang pegunungan menciptakan suasana yang sangat menenangkan dan cocok untuk melepas penat.",
            imageResource = R.drawable.menjer1, // Ganti dengan gambar Telaga Menjer
            latitude = -7.2875,
            longitude = 109.8792,
            harga = "Rp 5.000",
            jamBuka = "24 Jam"

        ),
        Wisata(
            id = 2,
            namaWisata = "Bukit Sikunir",
            rating = "4.9",
            lokasi = "Kepakisan, Wonosobo, Jawa Tengah",
            description = "Bukit Sikunir merupakan salah satu spot terbaik untuk menyaksikan golden sunrise di Wonosobo. Terletak di ketinggian sekitar 2.200 meter di atas permukaan laut, bukit ini menawarkan pemandangan matahari terbit yang spektakuler dengan latar belakang hamparan awan yang memukau.\n\nPendakian menuju puncak Sikunir membutuhkan waktu sekitar 30-45 menit melalui jalur yang relatif mudah. Dari puncak, pengunjung dapat melihat panorama 360 derajat yang meliputi dataran tinggi Dieng, deretan pegunungan, dan kabut pagi yang menyelimuti lembah. Pengalaman menyaksikan golden sunrise di Sikunir akan menjadi momen yang tak terlupakan.",
            imageResource = R.drawable.sikunir1, // Ganti dengan gambar Bukit Sikunir
            latitude = -7.1833,
            longitude = 109.9167,
            harga = "Rp 5.000",
            jamBuka = "04.00 - 07.00 WIB",
        ),
        Wisata(
            id = 3,
            namaWisata = "Gunung Prau",
            rating = "4.8",
            lokasi = "Dieng, Wonosobo, Jawa Tengah",
            description = "Gunung Prau adalah salah satu destinasi hiking favorit di Jawa Tengah dengan ketinggian 2.565 meter di atas permukaan laut. Gunung ini terkenal dengan padang rumput yang luas di puncaknya yang disebut 'Teletubbies Hill' karena kemiripannya dengan latar dalam film Teletubbies.\n\nPendakian Gunung Prau cocok untuk pemula karena jalurnya yang tidak terlalu sulit. Dari puncak, pengunjung dapat menikmati pemandangan sunrise yang menakjubkan, hamparan awan di bawah kaki, dan panorama pegunungan Dieng. Suhu di puncak bisa mencapai 5-10°C pada malam hari, sehingga sangat disarankan membawa pakaian hangat.",
            imageResource = R.drawable.prau1, // Ganti dengan gambar Gunung Prau
            latitude = -7.1858,
            longitude = 109.9364,
            harga = "Rp 15.000",
            jamBuka = "24 Jam",
        ),
        Wisata(
            id = 4,
            namaWisata = "Bukit Scooter",
            rating = "4.5",
            lokasi = "Dieng, Wonosobo, Jawa Tengah",
            description = "Bukit Scooter adalah destinasi wisata yang unik di Dieng, dinamakan demikian karena bentuk bukitnya yang menyerupai skuter dari kejauhan. Tempat ini menawarkan pemandangan yang sangat instagramable dengan latar belakang perbukitan Dieng yang hijau dan hamparan kebun kentang yang tertata rapi.\n\nBukit ini menjadi spot favorit untuk fotografi karena pemandangannya yang unik dan estetik. Pengunjung dapat menikmati udara sejuk khas dataran tinggi sambil berfoto dengan latar belakang pemandangan yang menawan. Waktu terbaik untuk berkunjung adalah pagi atau sore hari ketika cahaya matahari menciptakan kontras yang indah.",
            imageResource = R.drawable.scooter1, // Ganti dengan gambar Bukit Scooter
            latitude = -7.2047,
            longitude = 109.9081,
            harga = "Rp 5.000",
            jamBuka = "06.00 - 18.00 WIB",
        ),
        Wisata(
            id = 8,
            namaWisata = "Candi Arjuna",
            rating = "4.6",
            lokasi = "Dieng, Wonosobo, Jawa Tengah",
            description = "Kompleks Candi Arjuna merupakan salah satu situs candi Hindu tertua di Jawa, diperkirakan dibangun pada abad ke-7 hingga ke-8 Masehi. Kompleks ini terdiri dari lima candi yang diberi nama berdasarkan tokoh-tokoh dalam epos Mahabharata: Candi Arjuna, Candi Srikandi, Candi Puntadewa, Candi Sembadra, dan Candi Semar.\n\nCandi-candi ini merupakan bukti kejayaan Kerajaan Kalingga dan menunjukkan tingginya peradaban Hindu-Jawa pada masa lampau. Arsitektur candi yang sederhana namun elegan, dipadukan dengan latar belakang pegunungan Dieng yang indah, menciptakan suasana yang sangat spiritual dan menenangkan. Kompleks ini juga dilengkapi dengan museum yang menampilkan berbagai artefak peninggalan sejarah.",
            imageResource = R.drawable.arjuna1, // Ganti dengan gambar Candi Arjuna
            latitude = -7.2069,
            longitude = 109.9089,
            harga = "Rp 15.000",
            jamBuka = "06.00 - 17.00 WIB",
        ),
        Wisata(
            id = 6,
            namaWisata = "Kawah Sikidang",
            rating = "4.5",
            lokasi = "Dieng, Wonosobo, Jawa Tengah",
            description = "Kawah Sikidang adalah salah satu kawah aktif yang paling terkenal di kawasan Dieng Plateau. Nama 'Sikidang' berasal dari bahasa Jawa yang berarti 'kancil yang sedang melompat', merujuk pada aktivitas gas dan uap panas yang menyembur dari kawah mirip gerakan kancil yang lincah.\n\nKawah ini menawarkan pemandangan yang unik dengan semburan gas belerang dan uap panas yang keluar dari celah-celah tanah. Suhu di sekitar kawah bisa mencapai 60-70 derajat Celsius. Pengunjung dapat berjalan di sekitar kawah melalui jalur yang telah disediakan sambil menikmati fenomena geologi yang menakjubkan ini. Aroma belerang yang khas dan suara gemuruh dari dalam bumi menambah kesan mistis tempat ini.",
            imageResource = R.drawable.sikidang1, // Ganti dengan gambar Kawah Sikidang
            latitude = -7.2083,
            longitude = 109.9061,
            harga = "Rp 15.000",
            jamBuka = "06.00 - 17.00 WIB",
        ),

        Wisata(
            id = 5,
            namaWisata = "Telaga Warna",
            rating = "4.5",
            lokasi = "Dieng, Wonosobo, Jawa Tengah",
            description = "telaga warna.",
            imageResource = R.drawable.menjer2, // Ganti dengan gambar Kawah Sikidang
            latitude = -7.2083,
            longitude = 109.9061,
            harga = "Rp 15.000",
            jamBuka = "06.00 - 17.00 WIB",
        ),
        Wisata(
            id = 7,
            namaWisata = "Dieng Plateau",
            rating = "4.8",
            lokasi = "Kalimanah, Wonosobo, Jawa Tengah",
            description = "Dieng Plateau atau dataran tinggi Dieng, merupakan salah satu situs bersejarah paling terkenal di Jawa Tengah, Indonesia. Terletak di ketinggian 2.000 meter di atas permukaan laut, kawasan ini menawarkan pemandangan alam yang spektakuler dengan udara yang sejuk dan segar. Dieng terkenal dengan kompleks candi Hindu kuno dari abad ke-7 hingga ke-8 Masehi, yang merupakan bukti peradaban Jawa kuno yang megah.\n\nSelain candi bersejarah, Dieng juga memiliki berbagai fenomena alam yang menarik seperti kawah aktif, telaga berwarna-warni, dan hamparan kebun kentang yang hijau. Kawasan ini menjadi destinasi favorit wisatawan yang mencari ketenangan, petualangan, dan pengalaman spiritual yang mendalam. Dieng juga terkenal dengan fenomena embun upas yang terjadi pada musim kemarau.",
            imageResource = R.drawable.dieng_plateau_1, // Ganti dengan gambar Dieng Plateau
            latitude = -7.2094,
            longitude = 109.9036,
            harga = "Rp 15.000",
            jamBuka = "06.00 - 17.00 WIB",
        )
    )
    
    fun getAllWisata(): List<Wisata> {
        return wisataList
    }
    
    fun getWisataById(id: Int): Wisata? {
        android.util.Log.d("WisataRepository", "Looking for wisata with ID: $id")
        val result = wisataList.find { it.id == id }
        android.util.Log.d("WisataRepository", "Found wisata: ${result?.namaWisata ?: "null"}")
        return result
    }
    
    fun getWisataByName(name: String): Wisata? {
        return wisataList.find { it.namaWisata.equals(name, ignoreCase = true) }
    }
    
    fun searchWisata(query: String): List<Wisata> {
        return wisataList.filter { 
            it.namaWisata.contains(query, ignoreCase = true) ||
            it.lokasi.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
    }
}