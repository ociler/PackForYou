package com.packforyou.di

import com.packforyou.data.dataSources.FirebaseRemoteDatabaseImpl
import com.packforyou.data.dataSources.IFirebaseRemoteDatabase
import com.packforyou.data.repositories.IPackagesAndAtlasRepository
import com.packforyou.data.repositories.IUsersRepository
import com.packforyou.data.repositories.PackagesAndAtlasRepositoryImpl
import com.packforyou.data.repositories.UsersRepositoryImpl
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.atlas.IAtlasViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseDataSource(): IFirebaseRemoteDatabase {
        return FirebaseRemoteDatabaseImpl()
    }

    /******** LOGIN ************/
    @Singleton
    @Provides
    fun provideUsersRepository(): IUsersRepository {
        return UsersRepositoryImpl(provideFirebaseDataSource())
    }

}