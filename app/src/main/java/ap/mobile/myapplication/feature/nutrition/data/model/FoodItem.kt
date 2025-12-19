package ap.mobile.myapplication.feature.nutrition.data.model

data class FoodItem(
    val id: Int,
    val name: String,
    val kkal: Int,
    val protein: Double,
    val lemak: Double,
    val karbo: Double,
    val imageUrl: String? = null // Added for the image gallery
)