package ap.mobile.myapplication.feature.nutrition.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents a single meal item from TheMealDB API.
 */
data class MealDto(
    @SerializedName("idMeal")
    val id: String,

    @SerializedName("strMeal")
    val name: String,

    @SerializedName("strCategory")
    val category: String,

    @SerializedName("strArea")
    val area: String,

    @SerializedName("strMealThumb")
    val thumbnail: String // The URL for the meal image
)

/**
 * Represents the top-level response from TheMealDB API.
 */
data class MealResponse(
    @SerializedName("meals")
    val meals: List<MealDto>? // The list of meals can be null if no results are found
)
