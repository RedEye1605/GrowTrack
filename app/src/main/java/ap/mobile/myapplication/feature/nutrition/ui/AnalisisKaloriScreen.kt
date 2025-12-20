package ap.mobile.myapplication.feature.nutrition.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ap.mobile.myapplication.feature.nutrition.data.model.FoodItem
import ap.mobile.myapplication.feature.nutrition.viewmodel.AnalisisKaloriViewModel
import ap.mobile.myapplication.feature.nutrition.viewmodel.AnalisisKaloriState
import ap.mobile.myapplication.navigation.Screen
import ap.mobile.myapplication.core.ui.theme.MyApplicationTheme
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisKaloriScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AnalisisKaloriViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Analisis Kalori",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer


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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            AnalisisKaloriCard(state = uiState)
            Spacer(modifier = Modifier.height(16.dp))
            InsightCard(insight = uiState.insight)
            Spacer(modifier = Modifier.height(24.dp))
            RekomendasiMenu(
                rekomendasi = uiState.rekomendasiMenu,
                onRekomendasiClick = {
                    viewModel.toggleFoodSelection(it)
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    viewModel.saveDailyHistory()
                    navController.navigate(Screen.GrafikAnalisis.route) {
                        popUpTo(Screen.AnalisisKalori.route) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Simpan", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun AnalisisKaloriCard(modifier: Modifier = Modifier, state: AnalisisKaloriState) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Analisis Kalori Hari Ini", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.totalKalori,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = "Total Kalori", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                NutrientItem(state.protein, "Protein")
                NutrientItem(state.lemak, "Lemak")
                NutrientItem(state.karbo, "Karbo")
            }
        }
    }
}

@Composable
fun NutrientItem(amount: String, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = amount, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = name, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun InsightCard(modifier: Modifier = Modifier, insight: String) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = insight,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RekomendasiMenu(
    modifier: Modifier = Modifier,
    rekomendasi: List<FoodItem>,
    onRekomendasiClick: (FoodItem) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Rekomendasi Menu", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            rekomendasi.forEach { foodItem ->
                RekomendasiMenuItem(
                    foodItem = foodItem,
                    onClick = { onRekomendasiClick(foodItem) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekomendasiMenuItem(
    foodItem: FoodItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = foodItem.name, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
            Text(text = "${String.format(Locale.US, "%.0f", foodItem.protein)}g", fontSize = 10.sp, color = Color.Gray)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AnalisisKaloriScreenPreview() {
    MyApplicationTheme {
        val navController = rememberNavController()
        AnalisisKaloriScreen(navController = navController)
    }
}