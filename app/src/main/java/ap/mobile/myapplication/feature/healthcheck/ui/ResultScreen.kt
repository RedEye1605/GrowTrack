package ap.mobile.myapplication.feature.healthcheck.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ap.mobile.myapplication.feature.healthcheck.data.model.HealthCheckInput
import ap.mobile.myapplication.feature.healthcheck.data.model.HealthCheckSummary
import ap.mobile.myapplication.core.ui.theme.*
import ap.mobile.myapplication.feature.healthcheck.viewmodel.ResultViewModel
import ap.mobile.myapplication.core.ui.components.SimpleHeader
import ap.mobile.myapplication.core.ui.components.*
import ap.mobile.myapplication.feature.healthcheck.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavHostController,
    vm: ResultViewModel = viewModel()
) {
    // --- 1. SETUP DATA & STATE ---
    val input = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckInput>("healthInput")
    val isHistoryView = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isHistoryView") ?: false
    val summaryData = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckSummary>("fullSummary")
    val ui by vm.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // --- 2. SIDE EFFECTS ---
    LaunchedEffect(input) {
        if (input != null) {
            if (isHistoryView) vm.evaluate(input) else vm.evaluateAndSave(input)
        }
    }

    // --- 3. HANDLERS ---
    fun handleBackButton() {
        if (!isHistoryView) {
            val summary = HealthCheckSummary(
                timestamp = System.currentTimeMillis(),
                riskLevel = ui.riskLevel.name,
                riskScore = ui.riskScore,
                shortRecommendation = ui.recommendationShort,
                inputData = input
            )
            navController.previousBackStackEntry?.savedStateHandle?.set("healthResult", summary)
        }
        navController.popBackStack()
    }

    BackHandler { handleBackButton() }

    // --- 4. DIALOGS ---
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                if (summaryData?.id?.isNotEmpty() == true) {
                    vm.deleteHistory(summaryData.id) { navController.popBackStack() }
                }
            }
        )
    }

    // --- 5. MAIN ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHistoryView) "Detail Riwayat" else "Hasil Analisa",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleBackButton() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    if (isHistoryView) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                // SECTION 1: RISIKO & REKOMENDASI
                item {
                    RiskStatusCard(riskLevel = ui.riskLevel, riskScore = ui.riskScore)
                }

                item {
                    RecommendationCard(recommendation = ui.recommendationShort, riskLevel = ui.riskLevel)
                }

                // SECTION 2: DATA DETAIL
                if (input != null) {
                    item { InputDataCard(input) }
                }

                item {
                    Text(
                        "Detail Penilaian",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    AssessmentCard(
                        title = "Faktor Risiko Terdeteksi",
                        icon = Icons.Outlined.Analytics,
                        items = ui.reasons
                    )
                }

                if (input?.symptoms?.isNotEmpty() == true) {
                    item {
                        AssessmentCard(
                            title = "Gejala Dilaporkan",
                            icon = Icons.Outlined.MedicalServices,
                            items = input.symptoms
                        )
                    }
                }

                // SECTION 3: ACTIONS
                item {
                    ResultActionButtons(
                        isHistoryView = isHistoryView,
                        onBackHome = { handleBackButton() },
                        onShare = { /* Share Logic here */ }
                    )
                }
            }
        }
    }
}