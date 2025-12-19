package ap.mobile.myapplication.di

import ap.mobile.myapplication.core.data.api.RetrofitClient
import ap.mobile.myapplication.feature.growth.data.repository.AnalysisRepository

object Injection {
    fun provideRepository(): AnalysisRepository {
        val storage = com.google.firebase.storage.FirebaseStorage.getInstance()
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        return AnalysisRepository(storage, auth)
    }

    fun provideAuthRepository(): ap.mobile.myapplication.feature.auth.data.repository.AuthRepository {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        return ap.mobile.myapplication.feature.auth.data.repository.AuthRepository(auth)
    }
}
