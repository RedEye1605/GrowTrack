package ap.mobile.myapplication.feature.nutrition.data.network

import com.google.gson.annotations.SerializedName

/**
 * DTO for images from the Picsum Photos API.
 */
data class ImageDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("url")
    val url: String, // The web URL

    @SerializedName("download_url")
    val downloadUrl: String // The image URL we will use
)
