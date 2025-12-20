package ap.mobile.myapplication.feature.nutrition.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ap.mobile.myapplication.feature.nutrition.data.model.DailyHistory
import ap.mobile.myapplication.feature.nutrition.data.model.FoodItem
import ap.mobile.myapplication.feature.nutrition.data.repository.GiziFirestoreRepository
import java.text.SimpleDateFormat
import java.util.*

data class AnalisisKaloriState(
    val totalKalori: String = "0 kkal",
    val protein: String = "0.0 g",
    val lemak: String = "0.0 g",
    val karbo: String = "0.0 g",
    val insight: String = "Pilih menu untuk melihat analisis gizi.",
    val rekomendasiMenu: List<FoodItem> = emptyList()
)

class AnalisisKaloriViewModel : ViewModel() {
    private val repository = GiziFirestoreRepository()

    private val _selectedItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val selectedItems: StateFlow<List<FoodItem>> = _selectedItems.asStateFlow()

    private val allFoodItems: StateFlow<List<FoodItem>> = repository.getFoodItemsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<AnalisisKaloriState> = combine(
        _selectedItems,
        allFoodItems
    ) { selectedItems, allItems ->
        // Calculate totals from selected items
        val totalKkal = selectedItems.sumOf { it.kkal }
        val totalProtein = selectedItems.sumOf { it.protein }
        val totalLemak = selectedItems.sumOf { it.lemak }
        val totalKarbo = selectedItems.sumOf { it.karbo }

        // Generate insight
        val insight = if (totalProtein < 10) {
            "Asupan protein hari ini lebih rendah dari rata-rata kebutuhan balita seusianya. Tambahkan menu yang kaya protein seperti telur, ikan, ayam, atau tahu."
        } else if (totalProtein > 50) {
            "Asupan protein hari ini sudah sangat baik! Pertahankan pola makan ini."
        } else {
            "Asupan gizi hari ini sudah cukup seimbang."
        }

        // Generate recommendations
        val rekomendasi = if (totalProtein < 10) {
            // If protein is low, recommend high-protein foods
            val selectedIds = selectedItems.map { it.id }.toSet()
            allItems
                .filter { it.protein > 10 && it.id !in selectedIds }
                .take(3)
        } else {
            // If protein is sufficient, recommend other foods not yet selected
            val selectedIds = selectedItems.map { it.id }.toSet()
            allItems
                .filter { it.id !in selectedIds }
                .shuffled()
                .take(3)
        }

        // Create the state
        AnalisisKaloriState(
            totalKalori = "$totalKkal kkal",
            protein = "${String.format(Locale.US, "%.1f", totalProtein)} g",
            lemak = "${String.format(Locale.US, "%.1f", totalLemak)} g",
            karbo = "${String.format(Locale.US, "%.1f", totalKarbo)} g",
            insight = insight,
            rekomendasiMenu = rekomendasi
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalisisKaloriState() // Initial state before flows emit
    )

    fun toggleFoodSelection(foodItem: FoodItem) {
        _selectedItems.update { currentList ->
            if (currentList.contains(foodItem)) {
                currentList - foodItem
            } else {
                currentList + foodItem
            }
        }
    }

    fun saveDailyHistory() {
        viewModelScope.launch {
            try {
                val items = _selectedItems.value
                if (items.isEmpty()) return@launch // Don't save if nothing is selected

                val totalKkal = items.sumOf { it.kkal }
                val menuNames = items.map { it.name }

                val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
                val currentDate = sdf.format(Date())

                val dailyHistory = DailyHistory(
                    date = currentDate,
                    menus = menuNames,
                    totalKalori = totalKkal
                )
                repository.addDailyHistory(dailyHistory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}