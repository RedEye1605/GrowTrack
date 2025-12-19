package ap.mobile.myapplication.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ap.mobile.myapplication.feature.auth.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authResult = MutableStateFlow<Result<AuthResult>?>(null)
    val authResult: StateFlow<Result<AuthResult>?> = _authResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authResult.value = repository.signInWithEmail(email, password)
            _isLoading.value = false
        }
    }

    fun signUpWithEmail(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authResult.value = repository.signUpWithEmail(name, email, password)
            _isLoading.value = false
        }
    }
    
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _isLoading.value = true
            _authResult.value = repository.signInWithGoogle(account)
            _isLoading.value = false
        }
    }

    fun checkUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }
    
    fun resetAuthResult() {
        _authResult.value = null
    }

    fun logout() {
        repository.logout()
    }
}
