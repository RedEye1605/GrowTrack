# Analisis Pertumbuhan Fisik Bayi

Aplikasi Android untuk menganalisis pertumbuhan fisik bayi berdasarkan foto yang diunggah.

## Fitur Utama

### 1. Upload Gambar
- Ambil foto dari kamera
- Pilih foto dari galeri
- Preview gambar sebelum analisis

### 2. Proses Analisis
- Progress bar untuk menampilkan status analisis
- Estimasi waktu pemrosesan
- Indikator visual yang informatif

### 3. Hasil Pengukuran
- Menampilkan tinggi badan (cm)
- Menampilkan lingkar kepala (cm)
- Menampilkan berat badan (kg)
- Status pertumbuhan (Normal/Berisiko Stunting/Stunting)

### 4. Riwayat Pengukuran
- Menyimpan hasil pengukuran
- Melihat riwayat dengan thumbnail
- Detail setiap pengukuran

### 5. Rekomendasi
- Rekomendasi medis
- Rekomendasi gizi
- Aktivitas pendukung

## Teknologi yang Digunakan

- **Kotlin**: Bahasa pemrograman utama
- **Jetpack Compose**: UI modern dan deklaratif
- **Navigation Compose**: Navigasi antar halaman
- **ViewModel**: Manajemen state
- **Coil**: Loading gambar
- **Accompanist Permissions**: Handling permissions

## Struktur Navigasi

```
Upload (Route A) 
    ↓
Process (Route B) 
    ↓
Result (Route C) 
    ↓
    ├── History (Route D)
    └── Recommendation (Route E)
```

## Permissions yang Diperlukan

- `CAMERA`: Untuk mengambil foto bayi
- `READ_MEDIA_IMAGES`: Untuk membaca foto dari galeri (Android 13+)
- `READ_EXTERNAL_STORAGE`: Untuk membaca foto dari galeri (Android 12 ke bawah)
- `INTERNET`: Untuk loading gambar dengan Coil

## Cara Menjalankan

1. Buka project di Android Studio
2. Sync Gradle dependencies
3. Jalankan aplikasi di emulator atau device fisik
4. Berikan permission untuk kamera dan galeri saat diminta

## Catatan

- Saat ini analisis masih menggunakan data simulasi
- Untuk implementasi real, perlu integrasi dengan ML model untuk analisis gambar
- Hasil pengukuran disimpan dalam memory, belum persistent storage
