package com.packforyou.di

import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.repositories.IUsersRepository
import com.packforyou.data.repositories.UsersRepositoryImpl
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
    fun provideLoginRepository(): IUsersRepository {
        return UsersRepositoryImpl(provideLoginDataSource())
    }

    @Singleton
    @Provides
    fun provideLoginDataSource(): IFirebaseRemoteDatabase {
        return FirebaseRemoteDatabaseImpl()
    }
}