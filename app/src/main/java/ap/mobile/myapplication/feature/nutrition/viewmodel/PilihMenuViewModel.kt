package ap.mobile.myapplication.feature.nutrition.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ap.mobile.myapplication.feature.nutrition.data.model.FoodItem
import ap.mobile.myapplication.core.data.api.RetrofitClient
import ap.mobile.myapplication.feature.nutrition.data.model.MealDto

@OptIn(FlowPreview::class)
class PilihMenuViewModel : ViewModel() {

    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val foodItems: StateFlow<List<FoodItem>> = _foodItems.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    val queryToSearch = if (query.isBlank()) "a" else query
                    searchMeals(queryToSearch)
                }
        }
    }

    private fun searchMeals(query: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.mealDbApi.searchMeals(query)
                _foodItems.value = response.meals?.map { it.toFoodItem() } ?: emptyList()
            } catch (e: Exception) {
                Log.e("PilihMenuViewModel", "Error fetching meals for query: $query", e)
                _foodItems.value = emptyList()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun MealDto.toFoodItem(): FoodItem {
        return FoodItem(
            id = this.id.toInt(),
            name = this.name,
            imageUrl = this.thumbnail,

            kkal = 250,
            protein = 15.0,
            lemak = 8.0,
            karbo = 30.0
        )
    }
}