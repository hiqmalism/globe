package com.hiqmalism.mystorysubmission.di

import android.content.Context
import com.hiqmalism.mystorysubmission.data.resource.UserRepository
import com.hiqmalism.mystorysubmission.data.api.ApiConfig
import com.hiqmalism.mystorysubmission.data.pref.UserPreference
import com.hiqmalism.mystorysubmission.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}