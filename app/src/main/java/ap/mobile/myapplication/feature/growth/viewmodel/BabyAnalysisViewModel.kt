package ap.mobile.myapplication.feature.growth.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ap.mobile.myapplication.feature.growth.data.model.GrowthTip
import ap.mobile.myapplication.feature.growth.data.model.MeasurementData
import ap.mobile.myapplication.feature.growth.data.model.Recommendation
import ap.mobile.myapplication.feature.growth.data.model.StatusPertumbuhan
import ap.mobile.myapplication.core.data.api.NetworkResult
import ap.mobile.myapplication.feature.growth.data.repository.AnalysisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class BabyAnalysisViewModel(private val repository: AnalysisRepository) : ViewModel() {
    
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()
    
    private val _uiState = MutableStateFlow<UiState<MeasurementData>>(UiState.Idle)
    val uiState: StateFlow<UiState<MeasurementData>> = _uiState.asStateFlow()
    
    // Legacy support: keeping these distinct flows for UI compatibility where needed, 
    // but ultimately they should derive from UiState.
    private val _processingStatus = MutableStateFlow("Menunggu...")
    val processingStatus: StateFlow<String> = _processingStatus.asStateFlow()
    
    private val _currentMeasurement = MutableStateFlow<MeasurementData?>(null)
    val currentMeasurement: StateFlow<MeasurementData?> = _currentMeasurement.asStateFlow()
    
    private val _measurementHistory = MutableStateFlow<List<MeasurementData>>(emptyList())
    val measurementHistory: StateFlow<List<MeasurementData>> = _measurementHistory.asStateFlow()
    
    private val _currentRecommendation = MutableStateFlow<Recommendation?>(null)
    val currentRecommendation: StateFlow<Recommendation?> = _currentRecommendation.asStateFlow()
    
    private val _growthTips = MutableStateFlow(sampleGrowthTips())
    val growthTips: StateFlow<List<GrowthTip>> = _growthTips.asStateFlow()

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        viewModelScope.launch {
            repository.getHistoryFlow().collect { result ->
                if (result is NetworkResult.Success) {
                    result.data?.let {
                        _measurementHistory.value = it
                    }
                }
                // Handle error if needed
            }
        }
    }

    fun setSelectedImage(uri: Uri) {
        _selectedImageUri.value = uri
        _uiState.value = UiState.Idle
    }
    
    fun startAnalysis(imagePath: String?) {
        if (imagePath == null) {
            _uiState.value = UiState.Error("Image path is invalid")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _processingStatus.value = "Menganalisis gambar..."
            
            // Switch to IO for file operations
            withContext(kotlinx.coroutines.Dispatchers.IO) {
                val file = File(imagePath)
                if (!file.exists()) {
                     withContext(kotlinx.coroutines.Dispatchers.Main) {
                        _uiState.value = UiState.Error("File gambar tidak ditemukan/gagal dibaca")
                    }
                    return@withContext
                }

                when (val result = repository.analyzeImage(file)) {
                    is NetworkResult.Success -> {
                        withContext(kotlinx.coroutines.Dispatchers.Main) {
                            result.data?.let { data ->
                                _currentMeasurement.value = data
                                _currentRecommendation.value = data.recommendation ?: getFallbackRecommendation(data.statusPertumbuhan)
                                _uiState.value = UiState.Success(data)
                                _processingStatus.value = "Selesai"
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        withContext(kotlinx.coroutines.Dispatchers.Main) {
                            _uiState.value = UiState.Error(result.message ?: "Terjadi kesalahan")
                            _processingStatus.value = "Gagal"
                        }
                    }
                    is NetworkResult.Loading -> {
                        withContext(kotlinx.coroutines.Dispatchers.Main) {
                             _uiState.value = UiState.Loading
                        }
                    }
                }
            }
        }
    }

    // Helper to process URI in background to prevent ANR
    fun processAndAnalyze(context: android.content.Context, uri: Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            _uiState.value = UiState.Loading // Update state immediately (thread-safe)
            _processingStatus.value = "Memproses gambar..."
            
            val file = ap.mobile.myapplication.core.util.FileUtils.getFileFromUri(context, uri)
            if (file != null) {
                startAnalysis(file.absolutePath)
            } else {
                _uiState.value = UiState.Error("Gagal memproses gambar")
            }
        }
    }
    
    private fun getFallbackRecommendation(status: StatusPertumbuhan): Recommendation {
        return when (status) {
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
    }
    
    fun saveToHistory() {
        val measurement = _currentMeasurement.value ?: return
        val imageUri = _selectedImageUri.value
        
        viewModelScope.launch {
            _processingStatus.value = "Menyimpan..."
            val result = repository.saveMeasurement(measurement, imageUri)
            if (result is NetworkResult.Success) {
                fetchHistory()
                _processingStatus.value = "Tersimpan"
            } else if (result is NetworkResult.Error) {
                _processingStatus.value = "Gagal: ${result.message}"
            } else {
                _processingStatus.value = "Gagal simpan (Unknown)"
            }
        }
    }
    
    fun setCurrentMeasurement(measurement: MeasurementData) {
        _currentMeasurement.value = measurement
        _currentRecommendation.value = measurement.recommendation ?: getFallbackRecommendation(measurement.statusPertumbuhan)
        measurement.imageUri?.let {
            _selectedImageUri.value = Uri.parse(it)
        }
    }

    fun clearCurrentMeasurement() {
        _currentMeasurement.value = null
        _currentRecommendation.value = null
        _selectedImageUri.value = null
        _uiState.value = UiState.Idle
        _processingStatus.value = "Menunggu..."
    }

    fun cancelAnalysis() {
        _uiState.value = UiState.Idle
        _processingStatus.value = "Dibatalkan"
    }

    fun resetStatus() {
        _processingStatus.value = "Menunggu..."
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
