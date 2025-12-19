package ap.mobile.myapplication.feature.stunting.ui

import ap.mobile.myapplication.feature.stunting.data.model.ResultSummary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ap.mobile.myapplication.feature.stunting.data.model.HistoryRecord
import ap.mobile.myapplication.feature.stunting.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisStunting(
    navController: NavController
) {
    val viewModel: HistoryViewModel = viewModel()
    val historyList = viewModel.historyList
    var selectedHistory by remember { mutableStateOf<HistoryRecord?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var gender by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<ResultSummary?>("hasilRingkas", null)?.collect { result ->
            result?.let {
                val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val record = HistoryRecord(date = date, summary = it)
                viewModel.saveHistory(record)
                savedStateHandle.remove<ResultSummary>("hasilRingkas")
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Analisis Stunting") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                Text("Jenis Kelamin", style = MaterialTheme.typography.titleMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                ) {
                    RadioButton(selected = gender == "Laki-laki", onClick = { gender = "Laki-laki" })
                    Text("Laki-laki", modifier = Modifier.clickable { gender = "Laki-laki" })
                    Spacer(Modifier.width(24.dp))
                    RadioButton(selected = gender == "Perempuan", onClick = { gender = "Perempuan" })
                    Text("Perempuan", modifier = Modifier.clickable { gender = "Perempuan" })
                }

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Usia (bulan)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Berat Badan (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Tinggi Badan (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val ageInt = age.toIntOrNull() ?: 0
                        val weightF = weight.toFloatOrNull() ?: 0f
                        val heightF = height.toFloatOrNull() ?: 0f
                        navController.navigate("HasilAnalisis/$gender/$ageInt/$weightF/$heightF")
                        gender = ""; age = ""; weight = ""; height = ""
                    }
                ) {
                    Text("Mulai Analisis")
                }

                Spacer(Modifier.height(24.dp))
                Text("Riwayat", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }

            items(items = historyList, key = { it.id }) { record ->
                HistoryItem(
                    record = record,
                    onClick = { selectedHistory = record },
                    onDelete = {
                        viewModel.deleteHistory(record)
                        scope.launch { snackbarHostState.showSnackbar("Riwayat berhasil dihapus") }
                    }
                )
            }
        }

        if (selectedHistory != null) {
            AlertDialog(
                onDismissRequest = { selectedHistory = null },
                confirmButton = {
                    TextButton(onClick = { selectedHistory = null }) { Text("Tutup") }
                },
                text = { HistoryDetailCard(selectedHistory!!) }
            )
        }
    }
}

@Composable
fun HistoryItem(record: HistoryRecord, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Tanggal: ${record.date}", style = MaterialTheme.typography.bodySmall)
            Text("Kategori: ${record.summary.kategori}")
            Text("Z-score: %.2f".format(record.summary.zScore))
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Klik untuk detail", style = MaterialTheme.typography.labelSmall)
                TextButton(onClick = onDelete) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun HistoryDetailCard(data: HistoryRecord) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Detail Riwayat", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Text("Tanggal: ${data.date}")
        Text("Z-score: %.2f".format(data.summary.zScore))
        Text("Kategori: ${data.summary.kategori}")
        Spacer(Modifier.height(8.dp))
        Text("Analisis: ${data.summary.analisis}")
        Spacer(Modifier.height(8.dp))
        Text("Rekomendasi:", style = MaterialTheme.typography.titleSmall)
        data.summary.rekomendasi.forEach { Text("• $it") }
    }
}