package com.packforyou.di

import com.packforyou.api.DirectionsApiService
import com.packforyou.api.DistanceMatrixApiService
import com.packforyou.data.repositories.IPackagesAndAtlasRepository
import com.packforyou.data.repositories.PackagesAndAtlasRepositoryImpl
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.atlas.IAtlasViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val BASE_URL = "https://maps.googleapis.com/"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            //.addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideDirectionsApiService(retrofit: Retrofit): DirectionsApiService = retrofit.create(DirectionsApiService::class.java)


    @Singleton
    @Provides
    fun provideMatrixApiService(retrofit: Retrofit): DistanceMatrixApiService = retrofit.create(DistanceMatrixApiService::class.java)



    /******** PACKAGES ************/

    @Singleton
    @Provides
    fun providePackagesAndAtlasRepository(retrofit: Retrofit): IPackagesAndAtlasRepository {
        return PackagesAndAtlasRepositoryImpl(
            AppModule.provideFirebaseDataSource(),
            provideDirectionsApiService(retrofit),
            provideMatrixApiService(retrofit)
        )
    }
}