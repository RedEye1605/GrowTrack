package ap.mobile.myapplication.data

data class MeasurementData(
    val id: Long = System.currentTimeMillis(),
    val date: String,
    val tinggiBadan: Float, // cm
    val lingkarKepala: Float, // cm
    val beratBadan: Float, // kg
    val statusPertumbuhan: StatusPertumbuhan,
    val imageUri: String? = null
)

enum class StatusPertumbuhan {
    NORMAL,
    BERISIKO_STUNTING,
    STUNTING
}

data class Recommendation(
    val medis: String,
    val gizi: String,
    val aktivitas: String
)

// Lazy list second row content
data class GrowthTip(
    val id: String,
    val title: String,
    val detail: String,
    val emoji: String
)
