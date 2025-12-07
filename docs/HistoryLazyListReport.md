# Layout List: HistoryScreen (Route D)

## Use Case
Fitur "Riwayat Pengukuran" menampilkan seluruh hasil analisis pertumbuhan bayi (tinggi, berat, lingkar kepala) serta insight harian agar orang tua bisa meninjau progres. Kebutuhan data list:
- **Measurement History**: kumpulan `MeasurementData` disimpan ViewModel setelah analisis selesai.
- **Growth Tips**: kumpulan `GrowthTip` statis yang menyampaikan edukasi singkat.

## Layout Activity (Composable)
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyList: List<MeasurementData>,
    growthTips: List<GrowthTip>,
    onBackClick: () -> Unit,
    onItemClick: (MeasurementData) -> Unit
)
```
Elemen utama:
- `Scaffold` dengan `TopAppBar` biru (ikon back) dan `SnackbarHost` untuk feedback.
- Body menggunakan `LazyColumn` + `Modifier.padding(innerPadding)` agar konten tidak tertutup app bar.
- Section 1 (insight banner) memakai `Surface` untuk memisahkan tips informatif.
- Section 2 (`items(growthTips)`) menampilkan daftar tips.
- Section 3 judul "Riwayat Tersimpan".
- Section 4 (`items(historyList)`) menampilkan daftar riwayat.
- Seluruh layar memakai background `Color(0xFFF5F7FA)` supaya list mudah dibaca.

## RowView / ItemView
### 1. `GrowthTipRow`
Row edukasi tanpa gambar bayi. Menggunakan `Card` + `Row` dengan emoji sebagai ikon, lalu judul & detail.
```kotlin
@Composable
private fun GrowthTipRow(tip: GrowthTip)
```
- Emoji berada di dalam `Surface` bundar biru muda.
- Kolom teks menggunakan gaya `bodyMedium` dengan warna abu untuk keterbacaan.
- Seluruh row clickable → memicu snackbar menampilkan detail tip.

### 2. `HistoryItemCard`
Row ini memuat gambar bayi (memenuhi syarat "melibatkan gambar"), ringkasan pengukuran, dan status pertumbuhan.
```kotlin
@Composable
private fun HistoryItemCard(
    measurement: MeasurementData,
    onClick: () -> Unit
)
```
- Thumbnail: jika `imageUri` tersedia pakai `Image` + `ContentScale.Crop`. Jika tidak, tampilkan emoji 👶 dalam `Surface` biru.
- Info utama: tanggal + status badge dengan ikon (Normal/Warning/Error) menggunakan warna sesuai status.
- Ringkasan metrik disusun `Row` 3 kolom (`MeasurementSmallCard`) agar mudah dibandingkan.
- Setiap card clickable, menampilkan snackbar `"{tanggal} • {tinggi}"` lalu memanggil `onItemClick`.

Preview Compose sudah tersedia (`HistoryScreenPreview`) untuk memverifikasi desain terhadap mockup.

## LazyList State Handling
State list berada pada `BabyAnalysisViewModel` sehingga aman saat rotasi perangkat.
```kotlin
class BabyAnalysisViewModel : ViewModel() {
    private val _measurementHistory = MutableStateFlow<List<MeasurementData>>(emptyList())
    val measurementHistory: StateFlow<List<MeasurementData>> = _measurementHistory.asStateFlow()

    private val _growthTips = MutableStateFlow(sampleGrowthTips())
    val growthTips: StateFlow<List<GrowthTip>> = _growthTips.asStateFlow()
}
```
`HistoryScreen` menerima state melalui Navigation Graph:
```kotlin
val measurementHistory by viewModel.measurementHistory.collectAsState()
val growthTips by viewModel.growthTips.collectAsState()

composable(Screen.History.route) {
    HistoryScreen(historyList = measurementHistory, growthTips = growthTips, ...)
}
```
Karena `StateFlow` disimpan di ViewModel, data tetap ada meskipun konfigurasi berubah (ViewModel surviving configuration changes). `LazyColumn` otomatis me-render ulang list ketika state berubah tanpa kehilangan data.

## Snackbar Interaction
- Ketuk item riwayat → `SnackbarHostState.showSnackbar("{tanggal} • {tinggi} cm")` + navigasi ke hasil detail.
- Ketuk tip → snackbar menampilkan detail tip.
Semua snackbar memakai `remember { SnackbarHostState() }` dan `rememberCoroutineScope()`.

## Screenshot
Jalankan aplikasi dan buka Route D setelah menambahkan minimal satu hasil analisis. Ambil tangkapan layar emulator dan simpan ke `docs/history_screen.png`, lalu lampirkan di laporan Anda.

## Cara Coba
```bash
cd C:/Users/Rhendy Saragih/AndroidStudioProjects/MyApplication2
./gradlew :app:assembleDebug
```
Setelah build berhasil, jalankan aplikasi melalui Android Studio, lakukan analisis untuk mengisi riwayat, lalu buka layar Riwayat untuk memverifikasi dua lazy list serta snackbar.

