package com.packforyou.di

import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.repositories.ILoginRepository
import com.packforyou.data.repositories.LoginRepositoryImpl
import com.packforyou.ui.login.ILoginViewModel
import com.packforyou.ui.login.LoginViewModelImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /******** LOGIN ************/
    @Singleton
    @Provides
    fun provideLoginRepository(): ILoginRepository {
        return LoginRepositoryImpl(provideLoginDataSource())
    }

    @Singleton
    @Provides
    fun provideLoginDataSource(): IFirebaseRemoteDatabase {
        return FirebaseRemoteDatabaseImpl()
    }
}