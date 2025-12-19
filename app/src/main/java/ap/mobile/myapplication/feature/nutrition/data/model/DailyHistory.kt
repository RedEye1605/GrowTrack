package ap.mobile.myapplication.feature.nutrition.data.model

data class DailyHistory(
    val date: String,
    val menus: List<String>,
    val totalKalori: Int
)