package ap.mobile.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ap.mobile.myapplication.navigation.Screen
import ap.mobile.myapplication.core.ui.theme.MyApplicationTheme
import ap.mobile.myapplication.viewmodel.ViewModelFactory

// Feature Screens - Auth
import ap.mobile.myapplication.feature.auth.ui.LoginScreen
import ap.mobile.myapplication.feature.auth.ui.RegisterScreen
import ap.mobile.myapplication.feature.auth.viewmodel.AuthViewModel

// Feature Screens - Home
import ap.mobile.myapplication.feature.home.ui.HomeScreen
import ap.mobile.myapplication.feature.home.viewmodel.HomeViewModel

// Feature Screens - Growth / Baby Analysis
import ap.mobile.myapplication.feature.growth.viewmodel.BabyAnalysisViewModel
import ap.mobile.myapplication.feature.growth.viewmodel.UiState
import ap.mobile.myapplication.feature.growth.ui.*

// Feature Screens - Stunting
import ap.mobile.myapplication.feature.stunting.ui.AnalisisStunting
import ap.mobile.myapplication.feature.stunting.ui.HasilAnalisis

// Feature Screens - Article
import ap.mobile.myapplication.feature.article.ui.ArticleScreen
import ap.mobile.myapplication.feature.article.ui.ArticleDetailScreen
import ap.mobile.myapplication.feature.article.viewmodel.ArticleViewModel

// Feature Screens - Health Check
import ap.mobile.myapplication.feature.healthcheck.ui.InputScreen
import ap.mobile.myapplication.feature.healthcheck.ui.ResultScreen
// import ap.mobile.myapplication.feature.healthcheck.viewmodel.HealthCheckViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val viewModelFactory = ViewModelFactory.getInstance()

                    // ViewModels
                    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                    val babyAnalysisViewModel: BabyAnalysisViewModel = viewModel(factory = viewModelFactory)
                    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                    val articleViewModel: ArticleViewModel = viewModel(factory = viewModelFactory)
                    // Note: Ensure HealthCheckViewModel is available in Factory or instantiate regularly if not
                    // Assuming generic ViewModel usage for now or adding if Factory supports it. 
                    // If Factory doesn't support it, we might need a separate factory or default.
                    // For safety, I'll instantiate HealthCheckViewModel using defaults if possible, 
                    // or check if it's in the factory. Design says 'reusable', assuming Factory handles it for consistency.
                    
                    // Determine start destination
                    val startDestination = if (authViewModel.checkUserLoggedIn()) {
                         Screen.Home.route
                    } else {
                        Screen.Login.route
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        // --- Auth ---
                        composable(Screen.Login.route) {
                            LoginScreen(navController, authViewModel)
                        }
                        composable(Screen.Register.route) {
                            RegisterScreen(navController, authViewModel)
                        }

                        // --- Home ---
                        composable(Screen.Home.route) {
                            HomeScreen(
                                navController = navController,
                                articleViewModel = articleViewModel,
                                homeViewModel = homeViewModel
                            )
                        }

                        // --- Baby Analysis (Growth) ---
                        // Entry point for "Pertumbuhan"
                        composable(Screen.Growth.route) {
                            // Redirect "growth" to "upload" as the actual start screen
                            LaunchedEffect(Unit) {
                                navController.navigate(Screen.Upload.route) {
                                    popUpTo(Screen.Growth.route) { inclusive = true }
                                }
                            }
                        }
                        
                        composable(Screen.Upload.route) {
                            val selectedImageUri by babyAnalysisViewModel.selectedImageUri.collectAsState()
                            UploadImageScreen(
                                selectedImageUri = selectedImageUri,
                                onImageSelected = { uri -> babyAnalysisViewModel.setSelectedImage(uri) },
                                onStartAnalysis = {
                                    selectedImageUri?.let { uri ->
                                        // Use the new background processing method to avoid ANR
                                        babyAnalysisViewModel.processAndAnalyze(context, uri)
                                        navController.navigate(Screen.Process.route)
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        
                        composable(Screen.Process.route) {
                            val uiState by babyAnalysisViewModel.uiState.collectAsState()
                            val processingStatus by babyAnalysisViewModel.processingStatus.collectAsState()
                            
                            ProcessAnalysisScreen(
                                uiState = uiState,
                                statusMessage = processingStatus,
                                onCancel = {
                                    babyAnalysisViewModel.cancelAnalysis()
                                    navController.popBackStack()
                                }
                            )
                             LaunchedEffect(uiState) {
                                if (uiState is UiState.Success) {
                                    navController.navigate(Screen.MeasurementResult.route) {
                                        popUpTo(Screen.Upload.route)
                                    }
                                }
                            }
                        }

                        composable(Screen.MeasurementResult.route) {
                            val currentMeasurement by babyAnalysisViewModel.currentMeasurement.collectAsState()
                             currentMeasurement?.let { measurement ->
                                ResultMeasurementScreen(
                                    measurement = measurement,
                                    onSaveToHistory = { babyAnalysisViewModel.saveToHistory() },
                                    onViewHistory = { navController.navigate(Screen.HistoryMeasurement.route) },
                                    onViewRecommendation = { navController.navigate(Screen.Recommendation.route) },
                                    onBackToHome = {
                                        babyAnalysisViewModel.clearCurrentMeasurement()
                                        navController.navigate(Screen.Home.route) {
                                             popUpTo(Screen.Home.route) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                        
                        composable(Screen.HistoryMeasurement.route) {
                             val historyList by babyAnalysisViewModel.measurementHistory.collectAsState()
                             val growthTips by babyAnalysisViewModel.growthTips.collectAsState()
                             
                             ap.mobile.myapplication.feature.growth.ui.HistoryScreen(
                                 historyList = historyList,
                                 growthTips = growthTips,
                                 onBackClick = { navController.popBackStack() },
                                 onItemClick = { measurement ->
                                     babyAnalysisViewModel.setCurrentMeasurement(measurement)
                                     navController.navigate(Screen.MeasurementResult.route)
                                 }
                             )
                        }

                        composable(Screen.Recommendation.route) {
                            val recommendation by babyAnalysisViewModel.currentRecommendation.collectAsState()
                            
                            if (recommendation != null) {
                                ap.mobile.myapplication.feature.growth.ui.RecommendationScreen(
                                    recommendation = recommendation!!,
                                    onBackClick = { navController.popBackStack() },
                                    onSaveRecommendation = { 
                                        // Save recommendation logic if needed, or just toast
                                        babyAnalysisViewModel.saveToHistory()
                                    },
                                    onBackToHome = {
                                         babyAnalysisViewModel.clearCurrentMeasurement()
                                         navController.navigate(Screen.Home.route) {
                                              popUpTo(Screen.Home.route) { inclusive = true }
                                         }
                                    }
                                )
                            } else {
                                // Handle null recommendation (e.g. navigate back or show loading)
                                LaunchedEffect(Unit) {
                                    navController.popBackStack()
                                }
                            }
                        }


                        // --- Stunting Analysis ---
                        composable(Screen.Stunting.route) {
                             AnalisisStunting(navController = navController)
                        }
                        
                        composable("AnalisisStunting") { // Legacy route support
                             AnalisisStunting(navController = navController)
                        }

                        composable(
                            route = "HasilAnalisis/{gender}/{age}/{weight}/{height}",
                            arguments = listOf(
                                navArgument("gender") { type = NavType.StringType },
                                navArgument("age") { type = NavType.IntType },
                                navArgument("weight") { type = NavType.FloatType },
                                navArgument("height") { type = NavType.FloatType }
                            )
                        ) { backStackEntry ->
                            val gender = backStackEntry.arguments?.getString("gender") ?: ""
                            val age = backStackEntry.arguments?.getInt("age") ?: 0
                            val weight = backStackEntry.arguments?.getFloat("weight") ?: 0f
                            val height = backStackEntry.arguments?.getFloat("height") ?: 0f

                            HasilAnalisis(
                                navController = navController,
                                gender = gender,
                                usiaBulan = age,
                                beratBadan = weight,
                                tinggiBadan = height
                            )
                        }


                        // --- Health Check (Cek Gejala) ---
                        composable(Screen.Input.route) {
                             InputScreen(navController = navController)
                        }
                        
                        composable(Screen.HealthResult.route) { 
                             ResultScreen(navController = navController)
                        }


                        // --- Articles ---
                        composable(Screen.Articles.route) {
                            ArticleScreen(navController, articleViewModel)
                        }
                        
                        composable(Screen.ArticleDetail.route) {
                             val article = navController.previousBackStackEntry?.savedStateHandle?.get<ap.mobile.myapplication.feature.article.data.model.Article>("selected_article")
                             if (article != null) {
                                 ArticleDetailScreen(navController = navController)
                             }
                        }
                        
                        // --- Nutrition (Gizi) ---
                        composable(Screen.Nutrition.route) {
                             // Redirect or show Nutrition Home
                             // Assuming AnalisisKaloriScreen is the start
                             ap.mobile.myapplication.feature.nutrition.ui.AnalisisKaloriScreen(navController = navController)
                        }
                         composable(Screen.AnalisisKalori.route) {
                             ap.mobile.myapplication.feature.nutrition.ui.AnalisisKaloriScreen(navController = navController)
                        }
                        // Add other nutrition screens as needed: PilihMenu, TambahMenu, GrafikAnalisis
                        
                    }
                }
            }
        }
    }
}
