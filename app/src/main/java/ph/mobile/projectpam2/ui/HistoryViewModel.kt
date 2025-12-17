package ph.mobile.projectpam2.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ph.mobile.projectpam2.data.HistoryRepository
import ph.mobile.projectpam2.data.HistoryRecord

class HistoryViewModel : ViewModel() {

    private val repository = HistoryRepository()

    val historyList = mutableStateListOf<HistoryRecord>()

    fun loadHistory() {
        viewModelScope.launch {
            try {
                val remote = repository.loadHistory()
                historyList.clear()
                historyList.addAll(remote)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveHistory(record: HistoryRecord) {
        viewModelScope.launch {
            try {
                repository.saveHistory(record)
                loadHistory()        // refresh list setelah save
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteHistory(record: HistoryRecord) {
        viewModelScope.launch {
            try {
                repository.deleteHistory(record)
                loadHistory()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


