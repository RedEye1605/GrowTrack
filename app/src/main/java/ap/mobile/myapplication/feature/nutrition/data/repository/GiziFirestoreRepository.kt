package ap.mobile.myapplication.feature.nutrition.data.repository

import ap.mobile.myapplication.feature.nutrition.data.model.DailyHistory
import ap.mobile.myapplication.feature.nutrition.data.model.FoodItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
// ... imports
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Firestore Repository
class GiziFirestoreRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val foodItemsCollection = db.collection("foodItems")
    private val dailyHistoryCollection = db.collection("dailyHistory")

    // Get Food Items as Flow
    fun getFoodItemsFlow(): Flow<List<FoodItem>> = callbackFlow {
        val listener = foodItemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val items = snapshot?.documents?.mapNotNull { doc ->
                try {
                    FoodItem(
                        id = doc.id.hashCode(),
                        name = doc.getString("name") ?: "",
                        kkal = doc.getLong("kkal")?.toInt() ?: 0,
                        protein = doc.getDouble("protein") ?: 0.0,
                        lemak = doc.getDouble("lemak") ?: 0.0,
                        karbo = doc.getDouble("karbo") ?: 0.0
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            trySend(items)
        }

        awaitClose { listener.remove() }
    }

    // Get Daily History as Flow
    fun getDailyHistoryFlow(): Flow<List<DailyHistory>> = callbackFlow {
        val listener = dailyHistoryCollection
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val history = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val menusRaw = doc.get("menus") as? List<*>
                        val menusList = menusRaw?.mapNotNull { it as? String } ?: emptyList()

                        DailyHistory(
                            date = doc.getString("date") ?: "",
                            menus = menusList,
                            totalKalori = doc.getLong("totalKalori")?.toInt() ?: 0
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(history)
            }

        awaitClose { listener.remove() }
    }

    // Add Food Item
    suspend fun addFoodItem(foodItem: FoodItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val data = hashMapOf(
                "name" to foodItem.name,
                "kkal" to foodItem.kkal,
                "protein" to foodItem.protein,
                "lemak" to foodItem.lemak,
                "karbo" to foodItem.karbo
            )
            foodItemsCollection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete Food Item
    suspend fun deleteFoodItem(foodItem: FoodItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Find document by matching fields
            val querySnapshot = foodItemsCollection
                .whereEqualTo("name", foodItem.name)
                .whereEqualTo("kkal", foodItem.kkal)
                .get()
                .await()

            querySnapshot.documents.firstOrNull()?.reference?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add Daily History
    suspend fun addDailyHistory(dailyHistory: DailyHistory): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val data = hashMapOf(
                "date" to dailyHistory.date,
                "menus" to dailyHistory.menus,
                "totalKalori" to dailyHistory.totalKalori,
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            dailyHistoryCollection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Initialize with default data (call once on first app launch)
    suspend fun initializeDefaultData() = withContext(Dispatchers.IO) {
        try {
            val snapshot = foodItemsCollection.limit(1).get().await()
            if (snapshot.isEmpty) {
                val defaultItems = listOf(
                    FoodItem(1, "Nasi putih (matang)", 180, 3.0, 0.2, 38.1),
                    FoodItem(2, "Ikan", 200, 22.0, 12.0, 0.0),
                    FoodItem(3, "Sayur Bayam", 23, 2.9, 0.4, 3.6),
                    FoodItem(4, "Buah Apel", 52, 0.3, 0.2, 14.0),
                    FoodItem(5, "Telur Rebus", 78, 6.0, 5.0, 0.6),
                    FoodItem(6, "Daging Sapi", 250, 26.0, 15.0, 0.0),
                    FoodItem(7, "Susu UHT", 61, 3.4, 3.3, 4.8)
                )

                defaultItems.forEach { addFoodItem(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}