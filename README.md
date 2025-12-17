# GrowTrack

GrowTrack adalah aplikasi Android yang dirancang untuk membantu orang tua dalam memantau kondisi gizi, kesehatan, dan risiko stunting pada balita dengan cara yang lebih akurat, terstruktur, dan mudah dipahami.

## Fitur Utama

1. **Analisis Pertumbuhan Fisik Bayi**  
   Memungkinkan orang tua untuk memantau pertumbuhan fisik bayi dengan mengunggah foto. Aplikasi menggunakan teknologi *computer vision* untuk mengukur tinggi badan, lingkar kepala, dan lingkar lengan bayi.

2. **Analisis Rencana Gizi & Pola Makan Balita**  
   Menghitung total nutrisi harian dari makanan yang dikonsumsi balita, dengan rincian kalori, protein, lemak, dan karbohidrat. Riwayat konsumsi ditampilkan dalam bentuk grafik untuk pemantauan jangka panjang.

3. **Health Risk Checker**  
   Memeriksa kondisi kesehatan harian balita berdasarkan parameter seperti suhu tubuh, frekuensi muntah, nafsu makan, dan kondisi BAB. Hasil pemeriksaan memberikan kategori risiko (Rendah, Sedang, Tinggi) serta rekomendasi tindakan.

4. **Analisis Risiko Stunting Balita**  
   Menghitung nilai z-score berdasarkan usia, berat badan, tinggi badan, dan jenis kelamin untuk menilai risiko stunting pada balita, serta memberikan rekomendasi berdasarkan hasil analisis.

## Cara Kerja Aplikasi

1. **Analisis Pertumbuhan Fisik**  
   Unggah foto bayi untuk mengukur tinggi badan, lingkar kepala, dan lingkar lengan menggunakan *computer vision*. Aplikasi memberikan rekomendasi terkait pertumbuhan fisik berdasarkan hasil analisis.

2. **Analisis Gizi**  
   Pilih makanan atau tambahkan menu baru. Aplikasi menghitung total nutrisi secara otomatis dan menyimpan riwayat konsumsi harian.

3. **Health Risk Checker**  
   Masukkan data kesehatan harian. Aplikasi menilai kondisi balita berdasarkan pedoman WHO IMCI dan memberikan kategori risiko serta rekomendasi tindakan.

4. **Analisis Stunting**  
   Masukkan data pertumbuhan bayi. Aplikasi menghitung z-score dan memberikan hasil apakah bayi normal, berisiko stunting, atau stunting, lengkap dengan rekomendasi.

## Teknologi yang Digunakan

- **Jetpack Compose**: Membangun UI yang modern dan responsif.
- **Arsitektur MVVM**: Memisahkan logika dan tampilan untuk pengembangan aplikasi yang lebih terstruktur.
- **Navigation Compose**: Mengelola perpindahan antar-halaman.
- **StateFlow & ViewModel**: Menyinkronkan data antar-halaman secara otomatis.
- **Kotlin Coroutines**: Proses perhitungan dan evaluasi kesehatan secara asinkron.
- **Vico Chart Library**: Menampilkan grafik perkembangan nutrisi harian.
- **Firebase Firestore / Local Repository**: Menyimpan riwayat analisis pengguna.
- **MediaPipe Pose / TensorFlow Lite**: Untuk mendeteksi posisi tubuh bayi dan mengukur ukuran tubuh menggunakan *computer vision*.

## Cara Install

1. Clone repository ini:
   ```bash
   git clone https://github.com/username/GrowTrack.git
   ```

2. Buka proyek dengan Android Studio.

3. Jalankan aplikasi pada emulator atau perangkat Android Anda.

## Kontribusi

Kami menyambut kontribusi dari komunitas. Jika Anda ingin berkontribusi, silakan lakukan hal berikut:

1. Fork repository ini.
2. Buat branch baru untuk fitur/bugfix yang ingin Anda kerjakan.
3. Kirimkan pull request dengan deskripsi perubahan yang Anda buat.

## Lisensi

Aplikasi ini dilisensikan di bawah [MIT License](LICENSE).
