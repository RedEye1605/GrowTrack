package ap.mobile.myapplication.core.data.api

/**
 * Sealed class to represent network operation results.
 */
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}
