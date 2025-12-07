package ap.mobile.myapplication.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ap.mobile.myapplication.ui.screens.*
import ap.mobile.myapplication.viewmodel.BabyAnalysisViewModel

@Composable
fun BabyAnalysisNavGraph(
    navController: NavHostController,
    viewModel: BabyAnalysisViewModel
) {
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val processingProgress by viewModel.processingProgress.collectAsState()
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
                    viewModel.startAnalysis()
                    navController.navigate(Screen.Process.route)
                }
            )
        }
        
        // Route B: Process Analysis
        composable(Screen.Process.route) {
            ProcessAnalysisScreen(
                progress = processingProgress,
                statusMessage = processingStatus,
                onCancel = {
                    viewModel.cancelAnalysis()
                    navController.popBackStack()
                }
            )
            
            // Automatically navigate to result when processing is complete
            LaunchedEffect(isProcessing, processingProgress) {
                if (!isProcessing && processingProgress >= 1f && currentMeasurement != null) {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Upload.route)
                    }
                }
            }
        }
        
        // Route C: Result Measurement
        composable(Screen.Result.route) {
            currentMeasurement?.let { measurement ->
                ResultMeasurementScreen(
                    measurement = measurement,
                    onSaveToHistory = {
                        viewModel.saveToHistory()
                    },
                    onViewHistory = {
                        navController.navigate(Screen.History.route)
                    },
                    onViewRecommendation = {
                        navController.navigate(Screen.Recommendation.route)
                    },
                    onBackToHome = {
                        viewModel.clearCurrentMeasurement()
                        navController.navigate(Screen.Upload.route) {
                            popUpTo(Screen.Upload.route) { inclusive = true }
                        }
                    }
                )
            }
        }
        
        // Route D: History
        composable(Screen.History.route) {
            HistoryScreen(
                historyList = measurementHistory,
                growthTips = growthTips,
                onBackClick = {
                    navController.popBackStack()
                },
                onItemClick = { measurement ->
                    // Set current measurement and navigate to result
                    viewModel.setCurrentMeasurement(measurement)
                    navController.navigate(Screen.Result.route)
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
                        navController.navigate(Screen.Upload.route) {
                            popUpTo(Screen.Upload.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
