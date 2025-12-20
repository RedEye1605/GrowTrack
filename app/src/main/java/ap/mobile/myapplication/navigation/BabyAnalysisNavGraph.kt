package ap.mobile.myapplication.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ap.mobile.myapplication.feature.growth.ui.*
import ap.mobile.myapplication.feature.growth.viewmodel.*
import ap.mobile.myapplication.core.util.FileUtils
import ap.mobile.myapplication.feature.growth.viewmodel.BabyAnalysisViewModel
import ap.mobile.myapplication.feature.growth.viewmodel.UiState

@Composable
fun BabyAnalysisNavGraph(
    navController: NavHostController,
    viewModel: BabyAnalysisViewModel
) {
    val context = LocalContext.current
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val processingStatus by viewModel.processingStatus.collectAsState()
    val currentMeasurement by viewModel.currentMeasurement.collectAsState()
    val measurementHistory by viewModel.measurementHistory.collectAsState()
    val currentRecommendation by viewModel.currentRecommendation.collectAsState()
    val growthTips by viewModel.growthTips.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Upload.route
    ) {
        // Route A: Upload Image
        composable(Screen.Upload.route) {
            UploadImageScreen(
                selectedImageUri = selectedImageUri,
                onImageSelected = { uri ->
                    viewModel.setSelectedImage(uri)
                },
                onStartAnalysis = {
                    selectedImageUri?.let { uri ->
                        val file = FileUtils.getFileFromUri(context, uri)
                        if (file != null && file.exists()) {
                            viewModel.startAnalysis(file.absolutePath)
                            navController.navigate(Screen.Process.route)
                        } else {
                            android.widget.Toast.makeText(
                                context, 
                                "Gagal memproses gambar. Silakan coba lagi.", 
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Route B: Process Analysis
        composable(Screen.Process.route) {
            ProcessAnalysisScreen(
                uiState = uiState,
                statusMessage = processingStatus,
                onCancel = {
                    viewModel.cancelAnalysis()
                    navController.popBackStack()
                }
            )
            
            // Automatically navigate to result when processing is complete (Success state)
            LaunchedEffect(uiState) {
                if (uiState is UiState.Success) {
                    navController.navigate(Screen.MeasurementResult.route) {
                        popUpTo(Screen.Upload.route)
                    }
                }
            }
        }
        
        // Route C: Result Measurement
        composable(Screen.MeasurementResult.route) {
            currentMeasurement?.let { measurement ->
                ResultMeasurementScreen(
                    measurement = measurement,
                    onSaveToHistory = {
                        viewModel.saveToHistory()
                    },
                    onViewHistory = {
                        navController.navigate(Screen.HistoryMeasurement.route)
                    },
                    onViewRecommendation = {
                        navController.navigate(Screen.Recommendation.route)
                    },
                    onBackToHome = {
                        viewModel.clearCurrentMeasurement()
                        // Ensure we navigate back to the Main Home Screen (outside this nested graph if necessary, or just pop inclusive)
                        // Assuming Screen.Home.route is the main entry point defined in MainActivity
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
        
        // Route D: History
        composable(Screen.HistoryMeasurement.route) {
            HistoryScreen(
                historyList = measurementHistory,
                growthTips = growthTips,
                onBackClick = {
                    navController.popBackStack()
                },
                onItemClick = { measurement ->
                    // Set current measurement and navigate to result
                    viewModel.setCurrentMeasurement(measurement)
                    navController.navigate(Screen.MeasurementResult.route)
                }
            )
        }
        
        // Route E: Recommendation
        composable(Screen.Recommendation.route) {
            currentRecommendation?.let { recommendation ->
                RecommendationScreen(
                    recommendation = recommendation,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveRecommendation = {
                        // Navigate to Upload screen to select new photo for analysis
                        viewModel.clearCurrentMeasurement()
                        navController.navigate(Screen.Upload.route) {
                            popUpTo(Screen.Upload.route) { inclusive = true }
                        }
                    },
                    onBackToHome = {
                        viewModel.clearCurrentMeasurement()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
