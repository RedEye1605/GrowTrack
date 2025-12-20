package ap.mobile.myapplication.feature.nutrition.data.network

import com.google.gson.annotations.SerializedName

/**
 * DTO for posts from the JSONPlaceholder API.
 */
data class PostDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("userId")
    val userId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("body")
    val body: String
)
