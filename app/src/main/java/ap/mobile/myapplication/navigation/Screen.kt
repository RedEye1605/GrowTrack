package ap.mobile.myapplication.navigation

sealed class Screen(val route: String) {
    object Upload : Screen("upload")
    object Process : Screen("process")
    object Result : Screen("result")
    object History : Screen("history")
    object Recommendation : Screen("recommendation")
}
