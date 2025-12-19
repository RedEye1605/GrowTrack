package ap.mobile.myapplication.feature.growth.data.repository

import android.net.Uri
import ap.mobile.myapplication.feature.growth.data.model.MeasurementData
import ap.mobile.myapplication.feature.growth.data.model.StatusPertumbuhan
import ap.mobile.myapplication.core.data.api.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalysisRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    // Simulate AI Analysis (since we don't have a real backend model)
    // But we prepare the data as if it came from the server
    // Simulate AI Analysis (since we don't have a real backend model yet)
    // We check if the file exists to simulate "reading" it.
    suspend fun analyzeImage(imageFile: File): NetworkResult<MeasurementData> {
        return withContext(Dispatchers.IO) {
            try {
                if (!imageFile.exists()) {
                    return@withContext NetworkResult.Error("File gambar tidak ditemukan")
                }

                delay(3000) // Simulate generic AI processing time
                
                // Logic MOCK: Generate result based on random + some valid ranges
                // In a real app, we would upload this file to an endpoint (e.g., Cloud Functions or ML Kit)
                val mockResult = generateMockMeasurement(imageFile)
                NetworkResult.Success(mockResult)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Gagal menganalisis gambar")
            }
        }
    }

    suspend fun saveMeasurement(measurement: MeasurementData, imageUri: Uri?): NetworkResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid ?: return@withContext NetworkResult.Error("User tidak login")
                
                var finalImageUrl = measurement.imageUri
                
                // 1. Upload Image to Firebase Storage if uri exists
                if (imageUri != null) {
                    val storageRef = storage.reference.child("growth_images/${userId}/${System.currentTimeMillis()}")
                    val uploadTask = storageRef.putFile(imageUri).await()
                    finalImageUrl = storageRef.downloadUrl.await().toString()
                }

                // 2. Prepare Data
                // We use a new ID for the document, or use the one in measurement if valid
                val docRef = firestore.collection("users").document(userId)
                    .collection("growth_history").document()
                
                val finalMeasurement = measurement.copy(
                    id = System.currentTimeMillis(), // Ensure ID is fresh
                    imageUri = finalImageUrl
                )

                // 3. Save to Firestore
                docRef.set(finalMeasurement).await()
                
                NetworkResult.Success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                NetworkResult.Error("Gagal menyimpan data: ${e.message}")
            }
        }
    }

    suspend fun getHistory(): NetworkResult<List<MeasurementData>> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid ?: return@withContext NetworkResult.Error("User tidak login")
                
                val snapshot = firestore.collection("users").document(userId)
                    .collection("growth_history")
                    .orderBy("id", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val list = snapshot.documents.mapNotNull { it.toObject(MeasurementData::class.java) }
                NetworkResult.Success(list)
            } catch (e: Exception) {
                e.printStackTrace()
                NetworkResult.Error("Gagal memuat riwayat: ${e.message}")
            }
        }
    }
    
    // Helper to generate consistent mock data
    private fun generateMockMeasurement(imageFile: File): MeasurementData {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val currentDate = dateFormat.format(Date())
        
        // Randomize slightly for demo purposes
        val tinggiBadan = (60..80).random().toFloat()
        val lingkarKepala = (35..48).random().toFloat()
        val beratBadan = (5..12).random().toFloat()
        
        val status = when {
             tinggiBadan < 65 || beratBadan < 6 -> StatusPertumbuhan.STUNTING
             tinggiBadan < 70 || beratBadan < 7 -> StatusPertumbuhan.BERISIKO_STUNTING
             else -> StatusPertumbuhan.NORMAL
        }
        
        return MeasurementData(
            date = currentDate,
            tinggiBadan = tinggiBadan,
            lingkarKepala = lingkarKepala,
            beratBadan = beratBadan,
            statusPertumbuhan = status,
            imageUri = imageFile.absolutePath // Temporary local path
        )
    }
}
