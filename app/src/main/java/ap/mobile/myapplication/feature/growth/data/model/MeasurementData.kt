package ap.mobile.myapplication.feature.growth.data.model

data class MeasurementData(
    val id: Long = System.currentTimeMillis(),
    val date: String = "",
    val tinggiBadan: Float = 0f, // cm
    val lingkarKepala: Float = 0f, // cm
    val beratBadan: Float = 0f, // kg
    val statusPertumbuhan: StatusPertumbuhan = StatusPertumbuhan.NORMAL,
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
