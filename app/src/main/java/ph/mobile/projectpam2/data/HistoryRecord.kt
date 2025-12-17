package ph.mobile.projectpam2.data

import java.io.Serializable
import java.util.UUID
data class HistoryRecord(
    val id: String = UUID.randomUUID().toString(),
    val date: String = "",
    val summary: ResultSummary = ResultSummary()
)

data class ResultSummary(
  val zScore: Float = 0f,
   val kategori: String = "",
    val analisis: String = "",
    val rekomendasi: List<String> = emptyList()
) : Serializable



