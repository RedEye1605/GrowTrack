package ap.mobile.myapplication.navigation

/**
 * Unified Screen navigation definitions.
 * Consolidated from multiple features.
 */
sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")
    data object Register : Screen("register")

    // Baby Analysis Features
    data object Upload : Screen("upload")
    data object Process : Screen("process")
    data object MeasurementResult : Screen("measurement_result")
    data object HistoryMeasurement : Screen("history_measurement")
    data object Recommendation : Screen("recommendation")

    // Home
    data object Home : Screen("home")

    // Growth Analysis
    data object Growth : Screen("growth")

    // Nutrition Analysis (Gizi)
    data object Nutrition : Screen("nutrition")
    data object AnalisisKalori : Screen("analisis_kalori")
    data object GrafikAnalisis : Screen("grafik_analisis")
    data object PilihMenu : Screen("pilih_menu")
    data object TambahMenu : Screen("tambah_menu")

    // Health Check
    data object Input : Screen("input")
    data object HealthResult : Screen("health_result")
    data object Articles : Screen("articles")
    data object ArticleDetail : Screen("article_detail")

    // Stunting Analysis
    data object Stunting : Screen("stunting")
    data object AnalisisStunting : Screen("analisis_stunting")
    data object HasilAnalisis : Screen("hasil_analisis")
    data object History : Screen("history")
}
