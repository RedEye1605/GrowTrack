package ap.mobile.myapplication.feature.nutrition.data.network

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for food items from the network.
 * This class is used by Retrofit to parse the JSON response.
 */
data class FoodDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("calories")
    val calories: Double,

    @SerializedName("protein")
    val protein: Double,

    @SerializedName("fat")
    val fat: Double,

    @SerializedName("carbohydrates")
    val carbohydrates: Double
)
