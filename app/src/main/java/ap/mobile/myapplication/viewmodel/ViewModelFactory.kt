package ap.mobile.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ap.mobile.myapplication.feature.growth.data.repository.AnalysisRepository
import ap.mobile.myapplication.feature.growth.viewmodel.BabyAnalysisViewModel
import ap.mobile.myapplication.di.Injection

class ViewModelFactory(
    private val analysisRepository: AnalysisRepository,
    private val authRepository: ap.mobile.myapplication.feature.auth.data.repository.AuthRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BabyAnalysisViewModel::class.java)) {
            return BabyAnalysisViewModel(analysisRepository) as T
        } else if (modelClass.isAssignableFrom(ap.mobile.myapplication.feature.auth.viewmodel.AuthViewModel::class.java)) {
            return ap.mobile.myapplication.feature.auth.viewmodel.AuthViewModel(authRepository) as T
        } else if (modelClass.isAssignableFrom(ap.mobile.myapplication.feature.home.viewmodel.HomeViewModel::class.java)) {
            return ap.mobile.myapplication.feature.home.viewmodel.HomeViewModel() as T
        } else if (modelClass.isAssignableFrom(ap.mobile.myapplication.feature.article.viewmodel.ArticleViewModel::class.java)) {
            return ap.mobile.myapplication.feature.article.viewmodel.ArticleViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        
        fun getInstance(): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(),
                    Injection.provideAuthRepository()
                )
            }.also { instance = it }
    }
}
