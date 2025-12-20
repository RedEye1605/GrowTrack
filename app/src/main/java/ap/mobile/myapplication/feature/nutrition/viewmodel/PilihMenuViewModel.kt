package ap.mobile.myapplication.feature.nutrition.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ap.mobile.myapplication.feature.nutrition.data.model.FoodItem
import ap.mobile.myapplication.feature.nutrition.data.repository.GiziFirestoreRepository
import ap.mobile.myapplication.core.data.api.RetrofitClient
import ap.mobile.myapplication.feature.nutrition.data.model.MealDto
import java.util.Locale

@OptIn(FlowPreview::class)
class PilihMenuViewModel : ViewModel() {

    private val firestoreRepository = GiziFirestoreRepository()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Flow for API fetched items (debounced for search)
    private val apiFoodItems: Flow<List<FoodItem>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                flow {
                    try {
                        val response = RetrofitClient.mealDbApi.searchMeals(query)
                        emit(response.meals?.map { it.toFoodItem() } ?: emptyList())
                    } catch (e: Exception) {
                        Log.e("PilihMenuViewModel", "Error fetching meals from API for query: $query", e)
                        emit(emptyList())
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Combined flow for all food items (from Firestore and API), filtered by search query and sorted
    val foodItems: StateFlow<List<FoodItem>> = combine(
        firestoreRepository.getFoodItemsFlow(), // All local items from Firestore
        apiFoodItems, // API items, already filtered by searchQuery
        _searchQuery // To use for filtering firestore items locally
    ) { allFirestoreItems, apiFilteredItems, currentSearchQuery ->
        val filteredFirestoreItems = if (currentSearchQuery.isBlank()) {
            allFirestoreItems
        } else {
            allFirestoreItems.filter { it.name.contains(currentSearchQuery, ignoreCase = true) }
        }

        val combinedList = filteredFirestoreItems + apiFilteredItems

        // Deduplicate by name, preferring items from Firestore if names clash, then sort alphabetically
        combinedList
            .distinctBy { it.name.lowercase(Locale.getDefault()) }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun MealDto.toFoodItem(): FoodItem {
        return FoodItem(
            id = this.id.toInt(),
            name = this.name,
            imageUrl = this.thumbnail,
            // Placeholder values, consider fetching actual nutritional data if available in API
            kkal = 250,
            protein = 15.0,
            lemak = 8.0,
            karbo = 30.0
        )
    }

    fun deleteFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            try {
                firestoreRepository.deleteFoodItem(foodItem)
            } catch (e: Exception) {
                Log.e("PilihMenuViewModel", "Gagal menghapus item: ${foodItem.name}", e)
            }
        }
    }
}

