package ap.mobile.myapplication.feature.nutrition.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ap.mobile.myapplication.feature.nutrition.data.repository.GiziFirestoreRepository

class GrafikAnalisisViewModel : ViewModel() {

    private val repository = GiziFirestoreRepository()
    val dailyHistory = repository.getDailyHistoryFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val modelProducer = ChartEntryModelProducer()

    init {
        viewModelScope.launch {
            dailyHistory.collect { dailyHistoryList ->
                val entries = dailyHistoryList.reversed().mapIndexed { index, history ->
                    FloatEntry(
                        x = index.toFloat(),
                        y = history.totalKalori.toFloat()
                    )
                }
                modelProducer.setEntries(entries)
            }
        }
    }
}