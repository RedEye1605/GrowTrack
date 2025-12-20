package ap.mobile.myapplication.feature.nutrition.data.network

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
