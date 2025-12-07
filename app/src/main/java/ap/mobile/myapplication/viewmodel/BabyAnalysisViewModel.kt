package ap.mobile.myapplication.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ap.mobile.myapplication.data.GrowthTip
import ap.mobile.myapplication.data.MeasurementData
import ap.mobile.myapplication.data.Recommendation
import ap.mobile.myapplication.data.StatusPertumbuhan
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BabyAnalysisViewModel : ViewModel() {
    
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()
    
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()
    
    private val _processingProgress = MutableStateFlow(0f)
    val processingProgress: StateFlow<Float> = _processingProgress.asStateFlow()
    
    private val _processingStatus = MutableStateFlow("Memulai analisis...")
    val processingStatus: StateFlow<String> = _processingStatus.asStateFlow()
    
    private val _currentMeasurement = MutableStateFlow<MeasurementData?>(null)
    val currentMeasurement: StateFlow<MeasurementData?> = _currentMeasurement.asStateFlow()
    
    private val _measurementHistory = MutableStateFlow<List<MeasurementData>>(emptyList())
    val measurementHistory: StateFlow<List<MeasurementData>> = _measurementHistory.asStateFlow()
    
    private val _currentRecommendation = MutableStateFlow<Recommendation?>(null)
    val currentRecommendation: StateFlow<Recommendation?> = _currentRecommendation.asStateFlow()
    
    private val _growthTips = MutableStateFlow(sampleGrowthTips())
    val growthTips: StateFlow<List<GrowthTip>> = _growthTips.asStateFlow()

    fun setSelectedImage(uri: Uri) {
        _selectedImageUri.value = uri
    }
    
    fun startAnalysis() {
        viewModelScope.launch {
            _isProcessing.value = true
            _processingProgress.value = 0f
            
            // Simulasi proses analisis
            _processingStatus.value = "Mengidentifikasi pose bayi..."
            delay(1000)
            _processingProgress.value = 0.3f
            
            _processingStatus.value = "Menganalisis gambar..."
            delay(1500)
            _processingProgress.value = 0.6f
            
            _processingStatus.value = "Menghitung pengukuran..."
            delay(1500)
            _processingProgress.value = 0.9f
            
            _processingStatus.value = "Menyelesaikan analisis..."
            delay(500)
            _processingProgress.value = 1f
            
            // Generate hasil pengukuran (simulasi)
            generateMeasurementResult()
            
            _isProcessing.value = false
        }
    }
    
    private fun generateMeasurementResult() {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val currentDate = dateFormat.format(Date())
        
        // Simulasi hasil pengukuran dengan variasi random
        val tinggiBadan = (65..75).random().toFloat() + (0..9).random() / 10f
        val lingkarKepala = (40..45).random().toFloat() + (0..9).random() / 10f
        val beratBadan = (7..10).random().toFloat() + (0..9).random() / 10f
        
        // Tentukan status berdasarkan pengukuran
        val status = when {
            tinggiBadan < 68 || lingkarKepala < 42 || beratBadan < 8 -> StatusPertumbuhan.STUNTING
            tinggiBadan < 70 || lingkarKepala < 43 || beratBadan < 8.5f -> StatusPertumbuhan.BERISIKO_STUNTING
            else -> StatusPertumbuhan.NORMAL
        }
        
        val measurement = MeasurementData(
            date = currentDate,
            tinggiBadan = tinggiBadan,
            lingkarKepala = lingkarKepala,
            beratBadan = beratBadan,
            statusPertumbuhan = status,
            imageUri = _selectedImageUri.value?.toString()
        )
        
        _currentMeasurement.value = measurement
        generateRecommendation(status)
    }
    
    private fun generateRecommendation(status: StatusPertumbuhan) {
        val recommendation = when (status) {
            StatusPertumbuhan.NORMAL -> Recommendation(
                medis = "Lanjutkan pemeriksaan rutin setiap bulan untuk memantau pertumbuhan bayi.",
                gizi = "Berikan ASI eksklusif atau makanan pendamping ASI sesuai usia. Pastikan asupan gizi seimbang dengan protein, karbohidrat, dan lemak sehat.",
                aktivitas = "Lakukan stimulasi motorik melalui tummy time, bermain dengan mainan edukatif, dan interaksi sosial."
            )
            StatusPertumbuhan.BERISIKO_STUNTING -> Recommendation(
                medis = "Konsultasikan dengan dokter anak dalam waktu dekat untuk evaluasi lebih lanjut dan pemantauan intensif.",
                gizi = "Tingkatkan asupan protein hewani seperti telur, ikan, dan daging. Tambahkan sayuran hijau dan buah-buahan kaya vitamin.",
                aktivitas = "Perbanyak aktivitas fisik ringan dan stimulasi sensorik. Pastikan waktu tidur yang cukup (12-14 jam per hari)."
            )
            StatusPertumbuhan.STUNTING -> Recommendation(
                medis = "SEGERA konsultasikan dengan dokter anak atau ahli gizi untuk penanganan medis dan program intervensi gizi.",
                gizi = "Berikan makanan tinggi kalori dan protein. Pertimbangkan suplementasi gizi sesuai rekomendasi dokter. Berikan makan dalam porsi kecil namun sering.",
                aktivitas = "Lakukan terapi stimulasi tumbuh kembang. Konsultasikan dengan fisioterapis anak jika diperlukan."
            )
        }
        
        _currentRecommendation.value = recommendation
    }
    
    fun saveToHistory() {
        _currentMeasurement.value?.let { measurement ->
            _measurementHistory.value = listOf(measurement) + _measurementHistory.value
        }
    }
    
    fun setCurrentMeasurement(measurement: MeasurementData) {
        _currentMeasurement.value = measurement
        generateRecommendation(measurement.statusPertumbuhan)
        measurement.imageUri?.let {
            _selectedImageUri.value = Uri.parse(it)
        }
    }

    fun clearCurrentMeasurement() {
        _currentMeasurement.value = null
        _currentRecommendation.value = null
        _selectedImageUri.value = null
        _processingProgress.value = 0f
        _processingStatus.value = "Memulai analisis..."
    }

    fun cancelAnalysis() {
        _isProcessing.value = false
        _processingProgress.value = 0f
        _processingStatus.value = "Memulai analisis..."
    }
}

private fun sampleGrowthTips(): List<GrowthTip> = listOf(
    GrowthTip(
        id = "tip-1",
        title = "Waktu Tidur",
        detail = "Pastikan bayi tidur 12-14 jam per hari untuk mendukung hormon pertumbuhan",
        emoji = "😴"
    ),
    GrowthTip(
        id = "tip-2",
        title = "Vitamin D",
        detail = "Berjemur 15 menit sebelum jam 9 pagi membantu penyerapan kalsium",
        emoji = "☀️"
    ),
    GrowthTip(
        id = "tip-3",
        title = "Kegiatan Sensorik",
        detail = "Sediakan mainan tekstur berbeda untuk melatih koordinasi tangan",
        emoji = "🧸"
    )
)
