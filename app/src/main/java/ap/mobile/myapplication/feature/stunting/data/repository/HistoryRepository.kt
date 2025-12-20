package ap.mobile.myapplication.feature.stunting.data.repository

import ap.mobile.myapplication.feature.stunting.data.model.HistoryRecord
import ap.mobile.myapplication.core.data.api.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class HistoryRepository {

    private val api = RetrofitClient.firebaseApi
    private val auth by lazy { FirebaseAuth.getInstance() }

    suspend fun loadHistory(): List<HistoryRecord> {
        val user = auth.currentUser ?: return emptyList()
        val token = user.getIdToken(true).await().token ?: return emptyList()

        val map = api.getHistory(token) ?: emptyMap()

        return map.map { (key, value) ->
            if (value.id.isBlank()) {
                value.copy(id = key)
            } else {
                if (value.id != key) value.copy(id = key) else value
            }
        }
    }

    suspend fun saveHistory(record: HistoryRecord) {
        val user = auth.currentUser ?: throw Exception("User tidak login")
        val token = user.getIdToken(true).await().token ?: throw Exception("Gagal mendapatkan token auth")

        val fixed = if (record.id.isBlank()) {
            record.copy(id = java.util.UUID.randomUUID().toString())
        } else record

        api.putHistory(fixed.id, fixed, token)
    }

    suspend fun deleteHistory(record: HistoryRecord) {
        api.deleteHistory(record.id)
    }
}