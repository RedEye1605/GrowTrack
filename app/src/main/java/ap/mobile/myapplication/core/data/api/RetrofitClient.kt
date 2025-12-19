package ap.mobile.myapplication.core.data.api

import ap.mobile.myapplication.feature.stunting.data.model.HistoryRecord
import ap.mobile.myapplication.feature.growth.data.model.MeasurementData
import ap.mobile.myapplication.feature.article.data.model.Article
import ap.mobile.myapplication.feature.healthcheck.data.model.HealthCheckSummary
import ap.mobile.myapplication.feature.nutrition.data.model.MealResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * Unified Retrofit Client for all API services.
 * Consolidates Firebase RTDB, MealDB, and Health Check APIs.
 */
object RetrofitClient {

    // Base URLs
    private const val BASE_URL_FIREBASE = "https://growtrackapp-8bab5-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private const val BASE_URL_MEALDB = "https://www.themealdb.com/api/json/v1/1/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    // Firebase RTDB API (for History and HealthCheck)
    val firebaseApi: FirebaseApiService by lazy {
        createRetrofit(BASE_URL_FIREBASE).create(FirebaseApiService::class.java)
    }

    // MealDB API (for food data)
    val mealDbApi: MealDbApiService by lazy {
        createRetrofit(BASE_URL_MEALDB).create(MealDbApiService::class.java)
    }

    // Alias for backward compatibility
    val instance: FirebaseApiService get() = firebaseApi
}

/**
 * Firebase Realtime Database API Service.
 * Handles history records and health assessments.
 */
interface FirebaseApiService {

    // History Records
    @GET("history.json")
    suspend fun getHistory(): Map<String, HistoryRecord>?

    @POST("history.json")
    suspend fun postHistory(@Body body: HistoryRecord): Map<String, String>

    @PUT("history/{id}.json")
    suspend fun putHistory(@Path("id") id: String, @Body body: HistoryRecord): HistoryRecord

    @DELETE("history/{id}.json")
    suspend fun deleteHistory(@Path("id") id: String)

    // Articles
    @GET("articles.json")
    suspend fun getArticles(): List<Article>

    @GET("articles/{index}.json")
    suspend fun getArticleDetail(@Path("index") index: Int): Article

    // Health Assessments
    @POST("health_assessments.json")
    suspend fun saveHealthCheck(@Body summary: HealthCheckSummary): Map<String, String>

    @GET("health_assessments.json")
    suspend fun getHealthHistory(
        @Query("orderBy") orderBy: String = "\"\$key\"",
        @Query("limitToLast") limit: Int,
        @Query("endAt") endAt: String? = null
    ): Map<String, HealthCheckSummary>

    @DELETE("health_assessments/{id}.json")
    suspend fun deleteAssessment(@Path("id") id: String): Response<Void>

    // Image Analysis (placeholder - needs actual API endpoint)
    @Multipart
    @POST("analyze")
    suspend fun analyzeImage(@Part image: MultipartBody.Part): Response<MeasurementData>

    // Growth (Analysis) History - User Specific
    @POST("users/{userId}/growth_history.json")
    suspend fun saveGrowthMeasurement(
        @Path("userId") userId: String,
        @Body measurement: MeasurementData
    ): Map<String, String>

    @GET("users/{userId}/growth_history.json")
    suspend fun getGrowthHistory(
        @Path("userId") userId: String
    ): Map<String, MeasurementData>?
}

/**
 * MealDB API Service.
 * Provides food/meal data for nutrition analysis.
 */
interface MealDbApiService {

    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse
}
