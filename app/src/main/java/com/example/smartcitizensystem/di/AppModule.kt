package com.example.smartcitizensystem.di

import com.example.smartcitizensystem.data.repository.FirebaseAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthRepository(): FirebaseAuthRepository {
        return FirebaseAuthRepository()
    }
}