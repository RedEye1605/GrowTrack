package ap.mobile.myapplication.feature.nutrition.data.network

import com.google.gson.annotations.SerializedName

/**
 * Represents the top-level response from TheMealDB API.
 */
data class MealResponse(
    @SerializedName("meals")
    val meals: List<MealDto>? // The list of meals can be null if no results are found
)
