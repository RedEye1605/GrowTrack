package ap.mobile.myapplication.feature.nutrition.ui
import ap.mobile.myapplication.navigation.Screen
import ap.mobile.myapplication.feature.nutrition.viewmodel.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import ap.mobile.myapplication.feature.nutrition.data.model.DailyHistory
import ap.mobile.myapplication.core.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrafikAnalisisScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: GrafikAnalisisViewModel = viewModel()
) {
    val dailyHistory by viewModel.dailyHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Grafik dan Riwayat",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(


                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            GrafikSection(viewModel.modelProducer)
            Spacer(modifier = Modifier.height(24.dp))

            // Inlined RiwayatSection to apply weight to LazyColumn
            Text(text = "Riwayat", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f) // <-- This is the fix
            ) {
                items(dailyHistory) { item ->
                    RiwayatItem(item)
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Added space before the button

            Button(
                onClick = { navController.navigate(Screen.PilihMenu.route) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Analisis Gizi", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun GrafikSection(modelProducer: ChartEntryModelProducer) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Tahun", "Bulan", "Minggu")
    var selectedIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Grafik Analisis Kalori",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Box {
                Button(
                    onClick = { expanded = true },
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(items[selectedIndex])
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    items.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            text = { Text(text = s) },
                            onClick = {
                                selectedIndex = index
                                expanded = false
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Chart(
            chart = lineChart(),
            chartModelProducer = modelProducer,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
            modifier = Modifier.height(200.dp)
        )
    }
}

@Composable
fun RiwayatItem(history: DailyHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = history.menus.joinToString(), fontWeight = FontWeight.SemiBold)
                Text(text = history.date, fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Total Kalori", fontSize = 12.sp)
                Text(text = "${history.totalKalori} kkal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GrafikAnalisisScreenPreview() {
    MyApplicationTheme {
        val navController = rememberNavController()
        GrafikAnalisisScreen(navController = navController)
    }
}