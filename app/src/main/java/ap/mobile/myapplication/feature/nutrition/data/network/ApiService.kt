package ap.mobile.myapplication.feature.nutrition.data.network

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for TheMealDB API.
 */
interface ApiService {

    /**
     * Searches for meals by name.
     * @param query The search term (e.g., "Arrabiata").
     */
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse
}
