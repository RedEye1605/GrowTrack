# Panduan Navigasi Aplikasi Baby Analysis

## Alur Navigasi Lengkap

Aplikasi ini memiliki 5 screen utama dengan navigasi sebagai berikut:

### 1. **Upload Image Screen** (Layar Awal)
**Route:** `Screen.Upload.route`

**Fungsi:**
- Pilih foto dari kamera atau galeri
- Preview foto yang dipilih
- Mulai analisis

**Navigasi dari screen ini:**
- ✅ Klik tombol **"Mulai Analisis"** → Navigasi ke **Process Analysis Screen**

**Callbacks:**

- `onImageSelected`: Menyimpan URI foto yang dipilih
- `onStartAnalysis`: Memulai analisis dan navigasi ke ProcessAnalysisScreen

---

### 2. **Process Analysis Screen** (Layar Proses)
**Route:** `Screen.Process.route`

**Fungsi:**
- Menampilkan progress analisis (0-100%)
- Menampilkan status proses real-time
- Animasi loading yang menarik
- Tombol batalkan analisis

**Navigasi dari screen ini:**
- ✅ **Otomatis** setelah selesai (progress 100%) → Navigasi ke **Result Measurement Screen**
- ✅ Klik tombol **"Batalkan Analisis"** → Kembali ke **Upload Image Screen**

**Callbacks:**
- `onCancel`: Membatalkan proses dan kembali ke Upload Screen

**Catatan:**
- Navigasi otomatis menggunakan `LaunchedEffect` yang memantau `isProcessing` dan `processingProgress`
- Tombol cancel akan disabled jika progress sudah > 80%

---

### 3. **Result Measurement Screen** (Layar Hasil)
**Route:** `Screen.Result.route`

**Fungsi:**
- Menampilkan hasil pengukuran (tinggi badan, lingkar kepala, berat badan)
- Menampilkan status pertumbuhan (Normal, Berisiko Stunting, Stunting)
- Menampilkan foto yang dianalisis

**Navigasi dari screen ini:**
- ✅ Klik tombol **"Simpan ke Riwayat"** → Menyimpan data dan tetap di screen ini
- ✅ Klik tombol **"Lihat Riwayat"** → Navigasi ke **History Screen**
- ✅ Klik tombol **"Lihat Rekomendasi"** → Navigasi ke **Recommendation Screen**
- ✅ Klik tombol **"Kembali ke Beranda"** → Navigasi ke **Upload Image Screen** (clear data)

**Callbacks:**
- `onSaveToHistory`: Menyimpan measurement ke history
- `onViewHistory`: Navigasi ke History Screen
- `onViewRecommendation`: Navigasi ke Recommendation Screen
- `onBackToHome`: Clear data dan kembali ke Upload Screen

---

### 4. **History Screen** (Layar Riwayat)
**Route:** `Screen.History.route`

**Fungsi:**
- Menampilkan daftar riwayat pengukuran
- List dengan card untuk setiap measurement
- Klik item untuk melihat detail

**Navigasi dari screen ini:**
- ✅ Klik **Back button** (top bar) → Kembali ke screen sebelumnya
- ✅ Klik **item riwayat** → Navigasi ke **Result Measurement Screen** dengan data item tersebut

**Callbacks:**
- `onBackClick`: Kembali ke screen sebelumnya dengan `popBackStack()`
- `onItemClick`: Set measurement yang dipilih dan navigasi ke Result Screen

---

### 5. **Recommendation Screen** (Layar Rekomendasi)
**Route:** `Screen.Recommendation.route`

**Fungsi:**
- Menampilkan rekomendasi medis
- Menampilkan rekomendasi gizi
- Menampilkan aktivitas pendukung
- Catatan penting

**Navigasi dari screen ini:**
- ✅ Klik **Back button** (top bar) → Kembali ke screen sebelumnya
- ✅ Klik tombol **"Simpan Rekomendasi"** → Navigasi ke **Upload Image Screen** (untuk analisis baru)
- ✅ Klik tombol **"Kembali ke Beranda"** → Navigasi ke **Upload Image Screen** (clear data)

**Callbacks:**
- `onBackClick`: Kembali ke screen sebelumnya dengan `popBackStack()`
- `onSaveRecommendation`: Clear data dan kembali ke Upload Screen untuk analisis baru
- `onBackToHome`: Clear data dan kembali ke Upload Screen

---

## Flow Diagram Navigasi

```
┌─────────────────────┐
│  Upload Image       │ ◄─────────────────────────────┐
│  (Start Screen)     │                               │
└──────────┬──────────┘                               │
           │ Mulai Analisis                           │
           ▼                                           │
┌─────────────────────┐                               │
│  Process Analysis   │                               │
│  (Auto Progress)    │                               │
└──────────┬──────────┘                               │
           │ Auto (100%)                              │
           ▼                                           │
┌─────────────────────┐                               │
│  Result             │ ◄──────────────┐              │
│  Measurement        │                │              │
└──┬────────┬────────┬┘                │              │
   │        │        │                 │              │
   │        │        └─Lihat Rek──┐    │              │
   │        │                      │    │              │
   │        └─Lihat Riwayat─┐     │    │              │
   │                         │     │    │              │
   │ Kembali ke Beranda      │     │    │              │
   │                         ▼     ▼    │              │
   │                    ┌─────────────────────┐        │
   │                    │    History          │        │
   │                    └──────────┬──────────┘        │
   │                               │ Click Item        │
   │                               └───────────────────┘
   │                         ┌─────────────────────┐
   │                         │  Recommendation     │
   │                         └──────────┬──────────┘
   │                                    │ Simpan/Kembali
   └────────────────────────────────────┴───────────────┘
```

---

## Implementasi Teknis

### File Kunci:
1. **BabyAnalysisNavGraph.kt** - Mengatur semua navigasi dengan NavHost
2. **Screen.kt** - Mendefinisikan route untuk setiap screen
3. **BabyAnalysisViewModel.kt** - Mengelola state dan data antar screen

### ViewModel Functions yang Penting:
```kotlin
viewModel.setSelectedImage(uri)          // Set foto yang dipilih
viewModel.startAnalysis()                // Mulai proses analisis
viewModel.saveToHistory()                // Simpan ke riwayat
viewModel.setCurrentMeasurement(data)    // Set measurement untuk ditampilkan
viewModel.clearCurrentMeasurement()      // Hapus data saat ini
viewModel.cancelAnalysis()               // Batalkan analisis
```

### Navigation Patterns yang Digunakan:

#### 1. **Navigate Forward:**
```kotlin
navController.navigate(Screen.Process.route)
```

#### 2. **Navigate with Clear Back Stack:**
```kotlin
navController.navigate(Screen.Upload.route) {
    popUpTo(Screen.Upload.route) { inclusive = true }
}
```

#### 3. **Navigate Back:**
```kotlin
navController.popBackStack()
```

#### 4. **Auto Navigation with LaunchedEffect:**
```kotlin
LaunchedEffect(isProcessing, processingProgress) {
    if (!isProcessing && processingProgress >= 1f) {
        navController.navigate(Screen.Result.route) {
            popUpTo(Screen.Upload.route)
        }
    }
}
```

---

## Tips Penggunaan:

1. **Dari Upload ke Process**: User harus memilih foto dulu sebelum tombol "Mulai Analisis" aktif
2. **Dari Process ke Result**: Navigasi otomatis setelah progress mencapai 100%
3. **Cancel Analysis**: Hanya bisa dilakukan jika progress < 80%
4. **View History**: Bisa diakses dari Result Screen setelah menyimpan
5. **View Recommendation**: Otomatis dibuat berdasarkan status pertumbuhan
6. **Kembali ke Home**: Semua data akan di-clear untuk analisis baru

---

## State Management:

Semua state disimpan di **BabyAnalysisViewModel**:
- `selectedImageUri` - URI foto yang dipilih
- `isProcessing` - Status proses analisis
- `processingProgress` - Progress analisis (0f - 1f)
- `processingStatus` - Pesan status saat ini
- `currentMeasurement` - Data hasil pengukuran
- `measurementHistory` - List semua riwayat
- `currentRecommendation` - Rekomendasi berdasarkan hasil

---

## Troubleshooting:

**Q: Tombol "Mulai Analisis" tidak aktif?**
A: Pastikan sudah memilih foto terlebih dahulu

**Q: Tidak bisa kembali dari Process Screen?**
A: Gunakan tombol "Batalkan Analisis" (hanya aktif jika progress < 80%)

**Q: Data measurement hilang setelah kembali ke home?**
A: Ini by design, simpan ke history terlebih dahulu jika ingin menyimpan data

**Q: History kosong?**
A: Klik "Simpan ke Riwayat" terlebih dahulu di Result Screen

---

Dibuat: November 2024
Versi: 1.0

