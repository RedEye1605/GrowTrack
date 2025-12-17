package ph.mobile.projectpam2.data

import ph.mobile.projectpam2.network.RetrofitClient

class HistoryRepository {

    private val api = RetrofitClient.api
    suspend fun loadHistory(): List<HistoryRecord> {
        val map = api.getHistory() ?: emptyMap()

        return map.map { (key, value) ->
            if (value.id.isBlank()) {
                value.copy(id = key)
            } else {
                if (value.id != key) value.copy(id = key) else value
            }
        }
    }

    suspend fun saveHistory(record: HistoryRecord) {
        val fixed = if (record.id.isBlank()) {
            record.copy(id = java.util.UUID.randomUUID().toString())
        } else record

        api.putHistory(fixed.id, fixed)
    }

    suspend fun deleteHistory(record: HistoryRecord) {
        api.deleteHistory(record.id)
    }
}
