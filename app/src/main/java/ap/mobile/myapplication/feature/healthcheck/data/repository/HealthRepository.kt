package ap.mobile.myapplication.feature.healthcheck.data.repository

import ap.mobile.myapplication.core.data.api.RetrofitClient
import ap.mobile.myapplication.feature.article.data.model.Article
import ap.mobile.myapplication.feature.healthcheck.data.model.HealthCheckSummary
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class HealthRepository {
    // Inisialisasi Retrofit API Client
    private val api = RetrofitClient.instance
    private val auth by lazy { FirebaseAuth.getInstance() }

    // 1. Ambil List Artikel
    suspend fun getArticles(): List<Article> {
        return api.getArticles()
    }

    // 2. Ambil Detail Artikel
    suspend fun getArticleDetail(index: Int): Article {
        return api.getArticleDetail(index)
    }

    // 3. Simpan Hasil Cek Kesehatan
    suspend fun saveHealthCheck(summary: HealthCheckSummary): Map<String, String> {
        // Get Token
        val user = auth.currentUser ?: throw Exception("User tidak login")
        val token = user.getIdToken(true).await().token ?: throw Exception("Gagal mendapatkan token")
        
        return api.saveHealthCheck(summary, token)
    }

    // 4. Ambil Riwayat
    suspend fun getHealthHistory(limit: Int = 5, endAtId: String? = null): List<HealthCheckSummary> {
        val user = auth.currentUser ?: return emptyList()
        val token = user.getIdToken(true).await().token ?: return emptyList()

        // Menyiapkan parameter endAt
        val endAtParam = if (endAtId != null) "\"$endAtId\"" else null
        val responseMap = api.getHealthHistory(limit = limit, endAt = endAtParam, auth = token)

        // Transformasi Map ke List
        val list = responseMap.map { (key, value) ->
            value.copy(id = key)
        }.sortedByDescending { it.timestamp }

        return if (endAtId != null) {
            list.filter { it.id != endAtId }
        } else {
            list
        }
    }

    // 5. Hapus Riwayat
    suspend fun deleteAssessment(id: String) {
        api.deleteAssessment(id)
    }
}