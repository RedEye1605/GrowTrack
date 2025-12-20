package ap.mobile.myapplication.feature.growth.data.repository

import android.net.Uri
import ap.mobile.myapplication.feature.growth.data.model.MeasurementData
import ap.mobile.myapplication.feature.growth.data.model.StatusPertumbuhan
import ap.mobile.myapplication.core.data.api.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import org.json.JSONObject


class AnalysisRepository(
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    // Initialize Gemini Model (Google AI SDK)
    // Requires API Key from https://aistudio.google.com/
    // TODO: Replace "YOUR_API_KEY" with your actual API Key.
    // It is recommended to store this in BuildConfig for security.
    private val generativeModel: com.google.ai.client.generativeai.GenerativeModel by lazy {
        com.google.ai.client.generativeai.GenerativeModel(
            // Use the classic 'gemini-1.5-flash' which is the widely supported free tier model.
            // Using the user-suggested preview model.
            modelName = "gemini-3-flash-preview",
            // PENTING: Ganti dengan API Key Anda sendiri!
            apiKey = "AIzaSyBA0ybOu28e4DpGgiJ3nCDb3JMgsUR26tU"
        )
    }

    suspend fun analyzeImage(imageFile: File): NetworkResult<MeasurementData> {
        return withContext(Dispatchers.IO) {
            try {
                if (!imageFile.exists()) {
                    return@withContext NetworkResult.Error("File gambar tidak ditemukan")
                }

                // Prepare input image
                val bitmap = android.graphics.BitmapFactory.decodeFile(imageFile.absolutePath)
                    ?: return@withContext NetworkResult.Error("Gagal membaca file gambar")

                // Construct Prompt
                val prompt = """
                    Analisis gambar bayi ini untuk tujuan pemantauan kesehatan (kesehatan balita).
                    Berikan output dalam format JSON murni (tanpa markdown ```json) dengan struktur berikut:
                    {
                      "estimasi_usia_bulan": (int, estimasi kasar visual),
                      "estimasi_berat_kg": (float, estimasi kasar),
                      "estimasi_tinggi_cm": (float, estimasi kasar),
                      "estimasi_lingkar_kepala_cm": (float, estimasi kasar),
                      "status_pertumbuhan": "NORMAL" | "BERISIKO_STUNTING" | "STUNTING",
                      "saran_medis": "Saran medis singkat...",
                      "saran_gizi": "Saran gizi detail...",
                      "saran_aktivitas": "Saran aktivitas/stimulasi..."
                    }
                    Jika tidak yakin, berikan estimasi terbaik berdasarkan visual. 
                    JANGAN berikan output selain JSON.
                """.trimIndent()

                val inputContent = com.google.ai.client.generativeai.type.content {
                    image(bitmap)
                    text(prompt)
                }

                // Call Gemini
                val response = generativeModel.generateContent(inputContent)
                val responseText = response.text ?: throw Exception("Gemini tidak memberikan respons teks")

                // Parse JSON
                // Robust parsing to handle potential markdown wrappers
                val cleanJson = responseText.replace("```json", "").replace("```", "").trim()
                val json = JSONObject(cleanJson)

                val statusString = json.optString("status_pertumbuhan", "NORMAL")
                
                // Parse dynamic values
                val estBerat = json.optDouble("estimasi_berat_kg", 9.5).toFloat()
                val estTinggi = json.optDouble("estimasi_tinggi_cm", 75.0).toFloat()
                val estKepala = json.optDouble("estimasi_lingkar_kepala_cm", 42.0).toFloat()
                
                // Parse Recommendations
                val recMedis = json.optString("saran_medis", "Konsultasikan dengan dokter.")
                val recGizi = json.optString("saran_gizi", "Berikan ASI/MPASI bergizi.")
                val recAktivitas = json.optString("saran_aktivitas", "Lakukan stimulasi rutin.")
                
                val aiRecommendation = ap.mobile.myapplication.feature.growth.data.model.Recommendation(
                    medis = recMedis,
                    gizi = recGizi,
                    aktivitas = recAktivitas
                )

                val status = when (statusString.uppercase()) {
                    "STUNTING" -> StatusPertumbuhan.STUNTING
                    "BERISIKO_STUNTING" -> StatusPertumbuhan.BERISIKO_STUNTING
                    else -> StatusPertumbuhan.NORMAL
                }

                // MOCK Measurement Data for now (Hybrid Approach: hard data mocked, soft data from AI)
                // In future, override these with MediaPipe results
                val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                val currentDate = dateFormat.format(Date())
                
                val resultData = MeasurementData(
                    date = currentDate,
                    tinggiBadan = estTinggi,
                    lingkarKepala = estKepala,
                    beratBadan = estBerat,
                    statusPertumbuhan = status,
                    imageUri = imageFile.absolutePath,
                    recommendation = aiRecommendation
                )
                
                NetworkResult.Success(resultData)

            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to mock if AI fails (during dev) or return Error
                NetworkResult.Error("Gagal menganalisis gambar: ${e.message}")
            }
        }
    }

    suspend fun saveMeasurement(measurement: MeasurementData, imageUri: Uri?): NetworkResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser ?: return@withContext NetworkResult.Error("User tidak login")
                val userId = user.uid

                // Get Auth Token
                val tokenResult = user.getIdToken(true).await()
                val token = tokenResult.token ?: return@withContext NetworkResult.Error("Gagal mengambil token autentikasi")

                var finalImageUrl = measurement.imageUri
                
                // 1. Upload Image to Firebase Storage if uri exists and is local
                // Fix: Using simple path and snapshot metadata to ensure file exists before getting URL
                if (imageUri != null && imageUri.scheme != "http" && imageUri.scheme != "https") {
                    try {
                        val filename = "${System.currentTimeMillis()}.jpg" // Simple filename
                        val storageRef = storage.reference.child("growth_images/$userId/$filename")
                        
                        val uploadTask = storageRef.putFile(imageUri).await()
                        // Use the storage reference from the completed task to get the download URL
                        finalImageUrl = uploadTask.storage.downloadUrl.await().toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Continue usage of local URI if upload leads to error (e.g. Storage bucket not created)
                        // This ensures the data is at least saved to the database.
                    }
                }

                // 2. Prepare Data
                val finalMeasurement = measurement.copy(
                    id = System.currentTimeMillis(),
                    imageUri = finalImageUrl
                )

                // 3. Save to Realtime Database via Retrofit WITH AUTH TOKEN
                ap.mobile.myapplication.core.data.api.RetrofitClient.instance.saveGrowthMeasurement(
                    userId = userId, 
                    measurement = finalMeasurement,
                    auth = token
                )
                
                NetworkResult.Success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                NetworkResult.Error("Gagal menyimpan data: ${e.message}")
            }
        }
    }

    fun getHistoryFlow(): Flow<NetworkResult<List<MeasurementData>>> = kotlinx.coroutines.flow.flow {
        val user = auth.currentUser
        if (user == null) {
            emit(NetworkResult.Error("User tidak login"))
            return@flow
        }

        try {
            // Get Auth Token
            val token = user.getIdToken(true).await().token ?: throw Exception("Gagal mengambil token")

            // Pass auth to GET request
            val responseMap = ap.mobile.myapplication.core.data.api.RetrofitClient.instance.getGrowthHistory(
                userId = user.uid,
                auth = token
            )
            
            val list = if (responseMap != null) {
                responseMap.values.toList().sortedByDescending { it.id }
            } else {
                emptyList()
            }
            
            emit(NetworkResult.Success(list))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Gagal memuat riwayat: ${e.message}"))
        }
    }
}
