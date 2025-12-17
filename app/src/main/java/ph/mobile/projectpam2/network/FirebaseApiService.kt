package ph.mobile.projectpam2.network

import ph.mobile.projectpam2.data.HistoryRecord
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface FirebaseApiService {

    @GET("history.json")
    suspend fun getHistory(): Map<String, HistoryRecord>?

    @PUT("history/{id}.json")
    suspend fun putHistory(
        @Path("id") id: String,
        @Body body: HistoryRecord
    ): HistoryRecord

    @DELETE("history/{id}.json")
    suspend fun deleteHistory(
        @Path("id") id: String
    )
}
