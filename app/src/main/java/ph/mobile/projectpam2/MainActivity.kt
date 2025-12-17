package ph.mobile.projectpam2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ph.mobile.projectpam2.ui.AnalisisStunting
import ph.mobile.projectpam2.ui.HasilAnalisis

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "AnalisisStunting"
            ) {
                composable("AnalisisStunting") {
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
            }
        }
    }
}
